package jobs;

import java.util.Arrays;
import java.util.List;

import models.Account;
import models.Bank;
import models.OneOffTransaction;
import models.Periodicity;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	private final int BANK_COUNT = 3;

	private final int ACCOUNT_COUNT = 1;

	private final int ACCOUNT_MAX_BALANCE = 10000;

	private final List<String> PERIODICITY_LABELS = Arrays.asList("Annuelle", "Semestrielle", "Trimestrielle", "Mensuelle");

	private final int TRANSACTION_MIN_AMOUNT = -200;

	private final int TRANSACTION_MAX_AMOUNT = 100;

	@Override
	public void doJob() {
		Logger.info("BEGIN doJob()");
		initBanks();
		initAccounts();
		initPeriodicities();
		initOneOffTransactions();
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
			for (int a = 0; a < ACCOUNT_COUNT; a++) {
				int index = (int) (Math.random() * BANK_COUNT);

				Account account = new Account();
				account.bank = Bank.<Bank>findAll().get(index);
				account.label = "Compte " + a;
				account.balance = Math.random() * ACCOUNT_MAX_BALANCE;
				account.lastSync = DateTime.now();
				account.save();
			}
		}
		Logger.info("  END initAccounts()");
	}

	private void initPeriodicities() {
		Logger.info("BEGIN initPeriodicities()");
		if (Periodicity.count() == 0) {
			for (String label : PERIODICITY_LABELS) {
				Periodicity periodicity = new Periodicity();
				periodicity.label = label;
				periodicity.save();
			}
		}
		Logger.info("  END initPeriodicities()");
	}

	private void initOneOffTransactions() {
		Logger.info("BEGIN initOneOffTransactions()");
		if (OneOffTransaction.count() == 0) {
			int currentYear = DateTime.now().getYear();

			for (Account account : Account.<Account>findAll()) {
				for (int y = currentYear - 3; y <= currentYear; y++) {
					for (int m = 1; m <= 12; m++) {
						int maxDays = new DateTime(y, m, 1, 0, 0).dayOfMonth().getMaximumValue();

						for (int d = 1; d <= maxDays; d++) {
							int maxDayTransactions = (int) (Math.random() * 6);

							for (int t = 0; t < maxDayTransactions; t++) {
								double amount = (Math.random() * TRANSACTION_MAX_AMOUNT + Math.abs(TRANSACTION_MAX_AMOUNT)) - TRANSACTION_MIN_AMOUNT;

								OneOffTransaction transaction = new OneOffTransaction();
								transaction.account = account;
								transaction.label = (amount >= 0) ? "Crédit" : "Débit";
								transaction.amount = amount;
								transaction.date = new DateTime(y, m, d, 0, 0);
								transaction.save();
							}
						}
					}
				}
			}
		}
		Logger.info("  END initOneOffTransactions()");
	}
}
