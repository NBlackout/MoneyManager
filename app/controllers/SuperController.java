package controllers;

import models.User;
import play.data.validation.Required;
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
			session.put("user.password", user.password);
			session.put("user.fullName", user.firstName + " " + user.lastName);
		} else {
			session.remove("user.id");
			session.remove("user.login");
			session.remove("user.password");
			session.remove("user.fullName");
		}
	}

	public static void logIn(String returnUrl, String login, String password) {
		validation.required(login).message("error.field.required");
		validation.required(password).message("error.field.required");

		User user = null;
		if (!validation.hasErrors()) {
			user = User.find("byLoginAndPassword", login, Crypto.encryptAES(password)).first();
			if (user == null) {
				validation.addError("login", "error.user.not.found");
			}
		}

		if (!validation.hasErrors()) {
			updateSession(user);
		} else {
			keepValidation();
		}

		redirectSafely(returnUrl);
	}

	public static void logOut(@Required String returnUrl) {
		updateSession(null);

		redirectSafely(returnUrl);
	}
}
