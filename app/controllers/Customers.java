package controllers;

import java.util.List;

import models.Bank;
import models.Customer;

import org.apache.commons.lang.StringUtils;

import play.libs.Crypto;

public class Customers extends SuperController {

	public static void index() {
		List<Customer> customers = Customer.findAll();

		render(customers);
	}

	public static void create() {
		List<Bank> banks = Bank.findAll();

		render(banks);
	}

	public static void edit(Long customerId) {
		Customer customer = Customer.findById(customerId);

		render(customer);
	}

	public static void save(Long customerId, Long bankId, String login, String password, String passwordRetyped, String firstName, String lastName) {
		/* Parameters validation */
		validation.required(bankId).message("errors.field.required");
		validation.required(login).message("errors.field.required");
		validation.required(password).message("errors.field.required");
		validation.required(passwordRetyped).message("errors.field.required");
		validation.required(firstName).message("errors.field.required");
		validation.required(lastName).message("errors.field.required");

		if (validation.hasError("passwordRetyped") == false && password.equals(passwordRetyped) == false) {
			validation.addError("passwordRetyped", "error.passwords.not.equals");
		}

		if (validation.hasErrors() == false) {
			/* Customer creation */
			Customer customer = (customerId != null) ? Customer.<Customer>findById(customerId) : new Customer();
			customer.bank = Bank.findById(bankId);
			customer.login = login;
			customer.password = Crypto.encryptAES(password);
			customer.firstName = StringUtils.capitalize(firstName);
			customer.lastName = lastName.toUpperCase();
			customer.save();

			index();
		} else {
			keepValidation();
			create();
		}
	}
}
