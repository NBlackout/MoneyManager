package controllers;

import java.util.List;

import models.User;

import org.apache.commons.lang.StringUtils;

import play.libs.Crypto;

public class Users extends SuperController {

	public static void index() {
		List<User> users = User.find("ORDER BY login ASC").fetch();

		render(users);
	}

	public static void create() {
		render();
	}

	public static void edit(Long userId) {
		User user = User.findById(userId);

		render(user);
	}

	public static void signUp() {
		if (session.contains("user.id")) {
			Application.index();
		}

		render();
	}

	public static void save(Long userId, String login, String passwordOld, String password, String passwordBis, String locale, Boolean admin, Boolean activated) {
		if (userId == null) {
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
				user.admin = admin;
				user.activated = activated;
				user.save();

				index();
			}

			keepValidation();

			if (session.contains("user.id") == true) {
				create();
			} else {
				signUp();
			}
		} else {
			User user = User.findById(userId);

			/* Parameters validation */
			validation.required(locale).message("errors.field.required");

			boolean passwordFlag = StringUtils.isBlank(passwordOld);
			if (passwordFlag == false) {
				validation.required(password).message("errors.field.required");
				validation.required(passwordBis).message("errors.field.required");

				if (Crypto.encryptAES(passwordOld).equals(user.password) == false) {
					validation.addError("passwordOld", "errors.password.old.wrong");
				}

				if (validation.hasError("password") == false && validation.hasError("passwordBis") == false && password.equals(passwordBis) == false) {
					validation.addError("passwordBis", "errors.passwords.not.equals");
				}
			}

			if (validation.hasErrors() == false) {
				/* User update */
				user.password = (passwordFlag == false) ? Crypto.encryptAES(password) : user.password;
				user.locale = locale;
				user.admin = admin;
				user.activated = activated;
				user.save();

				if (session.contains("user.id") && user.id == Long.parseLong(session.get("user.id"))) {
					SuperController.updateSession(user);
				}

				index();
			}

			keepValidation();
			edit(userId);
		}
	}

	public static void toggle(Long userId) {
		User user = User.findById(userId);
		user.activated = (user.activated == false);
		user.save();

		index();
	}

	public static void delete(Long userId) {
		User user = User.findById(userId);
		user.delete();

		index();
	}
}
