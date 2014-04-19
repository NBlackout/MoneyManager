package controllers;

import java.util.Calendar;
import java.util.List;

import models.Account;
import models.Bank;
import play.mvc.Controller;

public class Application extends Controller {

	public static void index() {
		Account.deleteAll();
		Bank.deleteAll();

		for (int i = 0; i < 3; i++) {
			Bank bank = new Bank();
			bank.label = "Banque " + i;
			bank.save();
		}

		for (int i = 0; i < 10; i++) {
			int index = (int) (Math.random() * Bank.count());

			Account account = new Account();
			account.bank = Bank.<Bank>findAll().get(index);
			account.label = "Compte " + i;
			account.balance = Math.random() * 10000;
			account.lastSync = Calendar.getInstance().getTimeInMillis();
			account.save();
		}

		List<Account> accounts = Account.findAll();

		render(accounts);
	}
}
