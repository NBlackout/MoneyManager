package controllers;

import java.util.List;

import jobs.synchronizers.AccountsSynchronizer;
import models.Bank;

public class Banks extends SuperController {

	public static void index() {
		List<Bank> banks = Bank.findAll();

		render(banks);
	}

	public static void synchronize(long bankId) {
		new AccountsSynchronizer(bankId).now();

		index();
	}
}
