package controllers;

import java.util.List;

import models.Bank;
import models.Customer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

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
		List<Bank> banks = Bank.findAll();

		render(customer, banks);
	}

	public static void save(Long customerId, Long bankId, String login, String password, String passwordBis, String firstName, String lastName) {
		if (customerId == null) {
			/* Parameters validation */
			validation.required(bankId).message("errors.field.required");
			validation.required(login).message("errors.field.required");
			validation.required(password).message("errors.field.required");
			validation.required(passwordBis).message("errors.field.required");
			validation.required(firstName).message("errors.field.required");
			validation.required(lastName).message("errors.field.required");

			if (validation.hasError("passwordBis") == false && password.equals(passwordBis) == false) {
				validation.addError("passwordBis", "errors.passwords.not.equals");
			}

			if (validation.hasErrors() == false) {
				/* Customer creation */
				Customer customer = new Customer();
				customer.bank = Bank.findById(bankId);
				customer.login = login;
				customer.password = Crypto.encryptAES(password);
				customer.firstName = WordUtils.capitalize(firstName.toLowerCase());
				customer.lastName = lastName.toUpperCase();
				customer.save();

				index();
			}

			keepValidation();
			create();
		} else {
			Customer customer = Customer.findById(customerId);

			boolean passwordEdition = StringUtils.isNotBlank(password);
			if (passwordEdition == true) {
				validation.required(passwordBis).message("errors.field.required");

				if (validation.hasError("passwordBis") == false && password.equals(passwordBis) == false) {
					validation.addError("passwordBis", "errors.passwords.not.equals");
				}
			}

			if (validation.hasErrors() == false) {
				/* User update */
				customer.password = (passwordEdition == true) ? Crypto.encryptAES(password) : customer.password;
				customer.firstName = WordUtils.capitalize(firstName.toLowerCase());
				customer.lastName = lastName.toUpperCase();
				customer.save();

				index();
			}

			keepValidation();
			edit(customerId);
		}
	}
}
