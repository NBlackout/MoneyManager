package controllers;

import java.util.Calendar;
import java.util.List;

import models.Account;
import models.Bank;
import models.Transaction;
import play.mvc.Controller;

public class Application extends Controller {

	public static void index() {
		Transaction.deleteAll();
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

	public static void showAccount(long accountId) {
		Account account = Account.findById(accountId);

		if (Transaction.count("account.id", accountId) == 0) {
			for (int i = 0; i < 50; i++) {
				Calendar now = Calendar.getInstance();
				now.add(Calendar.DATE, -i);

				int perDay = (int) (Math.random() * 6);
				for (int j = 0; j < perDay; j++) {
					double amount = Math.random() * 300 - 200;

					Transaction transaction = new Transaction();
					transaction.account = account;
					transaction.label = ((amount >= 0) ? "Débit" : "Crédit") + " " + (Transaction.count() + 1);
					transaction.amount = amount;
					transaction.date = now.getTimeInMillis();
					transaction.save();
				}
			}
		}

		List<Transaction> transactions = Transaction.find("account.id = :accountId order by date desc").setParameter("accountId", accountId).fetch();

		render(transactions);
	}
}
