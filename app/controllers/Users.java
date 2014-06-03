package controllers;

import java.util.List;

import models.User;
import play.libs.Crypto;

public class Users extends SuperController {

	public static void index() {
		List<User> users = User.findAll();

		render(users);
	}

	public static void signUp() {
		if (session.contains("user.id")) {
			Application.index();
		}

		render();
	}

	public static void save(String login, String password, String passwordBis, String locale) {
		/* Parameters validation */
		validation.required(login).message("errors.field.required");
		validation.required(password).message("errors.field.required");
		validation.required(passwordBis).message("errors.field.required");
		validation.required(locale).message("errors.field.required");

		if (validation.hasError("login") == false) {
			long count = User.count("byLogin", login);
			if (count != 0) {
				validation.addError("login", "errors.login.already.used");
			}
		}

		if (validation.hasError("password") == false && validation.hasError("passwordBis") == false && password.equals(passwordBis) == false) {
			validation.addError("passwordBis", "errors.passwords.not.equals");
		}

		if (validation.hasErrors() == false) {
			/* User creation */
			User user = new User();
			user.login = login;
			user.password = Crypto.encryptAES(password);
			user.locale = locale;
			user.admin = false;
			user.activated = false;
			user.save();

			Application.index();
		} else {
			keepValidation();
			signUp();
		}
	}

	public static void toggle(Long userId) {
		User user = User.findById(userId);

		user.activated = (user.activated == false);
		user.save();

		index();
	}
}
