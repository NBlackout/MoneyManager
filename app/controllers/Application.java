package controllers;

import play.mvc.Controller;

public class Application extends Controller {

	public static void index() {
		render();
	}

	public static void login(String login, String password) {
		session.put("user", login);

		index();
	}
}
