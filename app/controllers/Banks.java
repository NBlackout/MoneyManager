package controllers;

import java.util.List;

import jobs.parsers.BankParser;
import models.Bank;

public class Banks extends SuperController {

	public static void index() {
		List<Bank> banks = Bank.findAll();

		render(banks);
	}

	public static void synchronize(long bankId, String login, String password) {
		new BankParser(bankId, login, password).now();

		index();
	}
}
