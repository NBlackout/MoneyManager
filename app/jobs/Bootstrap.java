package jobs;

import java.util.Calendar;

import models.Account;
import models.Bank;
import models.Transaction;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() {
		initBanks();
		initAccounts();
	}

	private void initBanks() {
		if (Bank.count() == 0) {
			for (int i = 0; i < 3; i++) {
				Bank bank = new Bank();
				bank.label = "Banque " + i;
				bank.save();
			}
		}
	}

	private void initAccounts() {
		if (Account.count() == 0) {
			for (int i = 0; i < 10; i++) {
				int index = (int) (Math.random() * Bank.count());

				Account account = new Account();
				account.bank = Bank.<Bank>findAll().get(index);
				account.label = "Compte " + i;
				account.balance = Math.random() * 10000;
				account.lastSync = Calendar.getInstance().getTimeInMillis();
				account.save();

				for (int j = 0; j < 50; j++) {
					Calendar now = Calendar.getInstance();
					now.add(Calendar.DATE, -j);

					int perDay = (int) (Math.random() * 6);
					for (int k = 0; k < perDay; k++) {
						double amount = Math.random() * 300 - 200;

						Transaction transaction = new Transaction();
						transaction.account = account;
						transaction.label = ((amount >= 0) ? "Crédit" : "Débit") + " " + (Transaction.count() + 1);
						transaction.amount = amount;
						transaction.date = now.getTimeInMillis();
						transaction.save();
					}
				}
			}
		}
	}
}
