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

		if (validation.hasErrors() == false) {
			User user = User.find("byLoginAndPassword", login, Crypto.encryptAES(password)).first();
			if (user != null && user.activated == true) {
				updateSession(user);
				redirectSafely(returnUrl);
			} else if (user == null) {
				validation.addError("login", "errors.user.not.found");
			} else {
				validation.addError("login", "errors.user.not.activated");
			}
		}

		keepValidation();
		Application.index();
	}

	public static void logOut(String returnUrl) {
		updateSession(null);

		redirectSafely(returnUrl);
	}

	@Before
	protected static void checkSession() {
		if (session.contains("user.id") == false && request.controller.equals("SuperController") == false) {
			boolean applicationIndexAction = request.action.equals("Application.index");
			boolean usersSignUpAction = request.action.equals("Users.signUp");
			boolean usersSaveAction = request.action.equals("Users.save");

			if (applicationIndexAction == false && usersSignUpAction == false && usersSaveAction == false) {
				Application.index();
			}
		}
	}

	protected static void updateSession(User user) {
		if (user != null) {
			session.put("user.id", user.id);
			session.put("user.login", user.login);
			session.put("user.admin", user.admin);
			Lang.change(user.locale);
		} else {
			session.remove("user.id");
			session.remove("user.login");
			session.remove("user.admin");
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
