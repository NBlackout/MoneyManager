package controllers;

import models.User;
import play.i18n.Lang;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;

public class SuperController extends Controller {

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

	@Before
	protected static void checkSession() {
		if (session.contains("user.id") == false && request.controller.equals("SuperController") == false) {
			boolean applicationIndexAction = request.action.equals("Application.index");
			boolean usersController = request.controller.equals("Users");

			if (applicationIndexAction == false && usersController == false) {
				Application.index();
			}
		}
	}

	protected static void updateSession(User user) {
		if (user != null) {
			session.put("user.id", user.id);
			session.put("user.login", user.login);
			Lang.change(user.locale);
		} else {
			session.remove("user.id");
			session.remove("user.login");
			Lang.change("fr");
		}
	}

	protected static void keepValidation() {
		flash.keep();
		params.flash();
		validation.keep();
	}

	protected static void redirectSafely(String returnUrl) {
		redirect((returnUrl != null) ? returnUrl : Router.getFullUrl("Application.index"));
	}
}
