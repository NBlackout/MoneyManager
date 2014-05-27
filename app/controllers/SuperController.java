package controllers;

import models.Customer;
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

	protected static void updateSession(Customer customer) {
		if (customer != null) {
			session.put("customer.id", customer.id);
			session.put("customer.fullName", customer.firstName + " " + customer.lastName);
		} else {
			session.remove("customer.id");
			session.remove("customer.fullName");
		}
	}

	public static void logIn(String returnUrl, String login, String password) {
		validation.required(login).message("error.field.required");
		validation.required(password).message("error.field.required");

		Customer customer = Customer.find("byLoginAndPassword", login, Crypto.encryptAES(password)).first();
		if (validation.hasErrors() == false && customer != null) {
			updateSession(customer);
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
