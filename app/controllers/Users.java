package controllers;

import models.User;
import play.libs.Crypto;

public class Users extends SuperController {

	public static void signUp() {
		render();
	}

	public static void save(String login, String password, String passwordBis, String locale) {
		/* Parameters validation */
		validation.required(login).message("errors.field.required");
		validation.required(password).message("errors.field.required");
		validation.required(passwordBis).message("errors.field.required");
		validation.required(locale).message("errors.field.required");

		if (validation.hasError("passwordBis") == false && password.equals(passwordBis) == false) {
			validation.addError("passwordBis", "errors.passwords.not.equals");
		}

		if (validation.hasErrors() == false) {
			/* User creation */
			User user = new User();
			user.login = login;
			user.password = Crypto.encryptAES(password);
			user.locale = locale;
			user.save();

			SuperController.updateSession(user);
		} else {
			keepValidation();
			signUp();
		}
	}
}
