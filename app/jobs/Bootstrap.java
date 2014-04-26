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

	private final int BANK_COUNT = 3;

	private final int ACCOUNT_COUNT = 1;

	private final int ACCOUNT_MAX_BALANCE = 10000;

	private final int TRANSACTION_MIN_AMOUNT = -200;

	private final int TRANSACTION_MAX_AMOUNT = 100;

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
			for (int i = 0; i < BANK_COUNT; i++) {
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

			for (int a = 0; a < ACCOUNT_COUNT; a++) {
				int index = (int) (Math.random() * BANK_COUNT);

				Account account = new Account();
				account.bank = Bank.<Bank>findAll().get(index);
				account.label = "Compte " + a;
				account.balance = Math.random() * ACCOUNT_MAX_BALANCE;
				account.lastSync = DateTime.now();
				account.save();

				for (int y = currentYear - 3; y <= currentYear; y++) {
					for (int m = 1; m <= 12; m++) {
						int maxDays = new DateTime(y, m, 1, 0, 0).dayOfMonth().getMaximumValue();
						for (int d = 1; d <= maxDays; d++) {
							int maxDayTransactions = (int) (Math.random() * 6);
							for (int t = 0; t < maxDayTransactions; t++) {
								double amount = (Math.random() * TRANSACTION_MAX_AMOUNT + Math.abs(TRANSACTION_MAX_AMOUNT)) - TRANSACTION_MIN_AMOUNT;

								Transaction transaction = new Transaction();
								transaction.account = account;
								transaction.label = (amount >= 0) ? "Crédit" : "Débit";
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
