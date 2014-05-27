package controllers;

import models.User;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Router;

public class SuperController extends Controller {

	protected static void keepValidation() {
		flash.keep();
		params.flash();
		validation.keep();
	}

	protected static void redirectSafely(String returnUrl) {
		redirect((returnUrl != null) ? returnUrl : Router.getFullUrl("Application.index"));
	}

	protected static void updateSession(User user) {
		if (user != null) {
			session.put("user.id", user.id);
			session.put("user.login", user.login);
			session.put("user.locale", user.locale);
		} else {
			session.remove("user.id");
			session.remove("user.login");
			session.remove("user.locale");
		}
	}

	public static void logIn(String returnUrl, String login, String password) {
		validation.required(login).message("error.field.required");
		validation.required(password).message("error.field.required");

		User user = User.find("byLoginAndPassword", login, Crypto.encryptAES(password)).first();
		if (validation.hasErrors() == false && user != null) {
			updateSession(user);
		} else {
			keepValidation();
		}

		redirectSafely(returnUrl);
	}

	public static void logOut(String returnUrl) {
		updateSession(null);

		redirectSafely(returnUrl);
	}
}
