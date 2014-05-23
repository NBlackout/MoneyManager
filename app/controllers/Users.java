package controllers;

import models.User;

import org.apache.commons.lang.StringUtils;

import play.libs.Crypto;

public class Users extends SuperController {

	public static void signUp() {
		if (session.contains("user.id")) {
			Application.index();
		}

		render();
	}

	public static void register(String login, String password, String passwordRetype, String firstName, String lastName) {
		/* Parameters validation */
		validation.required(login).message("error.field.required");
		validation.required(password).message("error.field.required");
		validation.required(passwordRetype).message("error.field.required");
		validation.required(firstName).message("error.field.required");
		validation.required(lastName).message("error.field.required");

		if (!validation.hasError("passwordRetype") && !password.equals(passwordRetype)) {
			validation.addError("passwordRetype", "error.user.passwords.not.equals");
		}

		if (!validation.hasErrors()) {
			/* User creation */
			User user = new User();
			user.login = login;
			user.password = Crypto.encryptAES(password);
			user.firstName = StringUtils.capitalize(firstName);
			user.lastName = lastName.toUpperCase();
			user.save();

			SuperController.updateSession(user);
		}

		keepValidation();
		signUp();
	}
}
