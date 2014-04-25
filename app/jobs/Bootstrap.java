package jobs;

import models.Account;
import models.Bank;
import models.Transaction;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() {
		Logger.info("BEGIN doJob()");
		initBanks();
		initAccounts();
		Logger.info("  END doJob()");
	}

	private void initBanks() {
		Logger.info("BEGIN initBanks()");
		if (Bank.count() == 0) {
			for (int i = 0; i < 3; i++) {
				Bank bank = new Bank();
				bank.label = "Banque " + i;
				bank.save();
			}
		}
		Logger.info("  END initBanks()");
	}

	private void initAccounts() {
		Logger.info("BEGIN initAccounts()");
		if (Account.count() == 0) {
			int currentYear = DateTime.now().getYear();

			for (int a = 0; a < 1; a++) {
				int index = (int) (Math.random() * Bank.count());

				Account account = new Account();
				account.bank = Bank.<Bank>findAll().get(index);
				account.label = "Compte " + a;
				account.balance = Math.random() * 10000;
				account.lastSync = DateTime.now();
				account.save();

				for (int y = currentYear - 3; y <= currentYear; y++) {
					for (int m = 1; m <= 12; m++) {
						int maxDays = new DateTime(y, m, 1, 0, 0).dayOfMonth().getMaximumValue();
						for (int d = 1; d <= maxDays; d++) {
							int maxDayTransactions = (int) (Math.random() * 6);
							for (int t = 0; t < maxDayTransactions; t++) {
								double amount = Math.random() * 300 - 200;

								Transaction transaction = new Transaction();
								transaction.account = account;
								transaction.label = ((amount >= 0) ? "Crédit" : "Débit") + " " + (Transaction.count() + 1);
								transaction.amount = amount;
								transaction.dateTime = new DateTime(y, m, d, 0, 0);
								transaction.monthly = (Math.random() >= 0.5d);
								transaction.save();
							}
						}
					}
				}
			}
		}
		Logger.info("  END initAccounts()");
	}
}
