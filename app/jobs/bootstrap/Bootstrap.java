package jobs.bootstrap;

import models.Account;
import models.Bank;
import models.transactions.OneOffTransaction;
import models.transactions.Periodicity;
import models.transactions.RegularTransactionCategory;

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
		initPeriodicities();
		initCategories();
		initOneOffTransactions();
		Logger.info("  END doJob()");
	}

	private void initBanks() {
		Logger.info("BEGIN initBanks()");
		if (Bank.count() == 0) {
			for (int b = 0; b < BoostrapConfiguration.BANK_COUNT; b++) {
				Bank bank = new Bank();
				bank.label = "Banque " + (b + 1);
				bank.save();
			}
		}
		Logger.info("  END initBanks()");
	}

	private void initAccounts() {
		Logger.info("BEGIN initAccounts()");
		if (Account.count() == 0) {
			for (int a = 0; a < BoostrapConfiguration.ACCOUNT_COUNT; a++) {
				int index = (int) (Math.random() * BoostrapConfiguration.BANK_COUNT);

				Account account = new Account();
				account.bank = Bank.<Bank>findAll().get(index);
				account.label = "Compte " + (a + 1);
				account.balance = Math.random() * BoostrapConfiguration.ACCOUNT_MAX_BALANCE;
				account.lastSync = DateTime.now();
				account.save();
			}
		}
		Logger.info("  END initAccounts()");
	}

	private void initPeriodicities() {
		Logger.info("BEGIN initPeriodicities()");
		if (Periodicity.count() == 0) {
			for (String label : BoostrapConfiguration.PERIODICITY_LABELS) {
				Periodicity periodicity = new Periodicity();
				periodicity.label = label;
				periodicity.save();
			}
		}
		Logger.info("  END initPeriodicities()");
	}

	private void initCategories() {
		Logger.info("BEGIN initCategories()");
		if (Periodicity.count() == 0) {
			for (String label : BoostrapConfiguration.CATEGORY_LABELS) {
				RegularTransactionCategory category = new RegularTransactionCategory();
				category.label = label;
				category.save();
			}
		}
		Logger.info("  END initCategories()");
	}

	private void initOneOffTransactions() {
		Logger.info("BEGIN initOneOffTransactions()");
		if (OneOffTransaction.count() == 0) {
			DateTime now = DateTime.now();
			int currentYear = now.getYear();
			int currentMonth = now.getMonthOfYear();

			for (Account account : Account.<Account>findAll()) {
				for (int y = currentYear - BoostrapConfiguration.MAX_YEARS_HISTORY; y <= currentYear; y++) {
					for (int m = 1; m <= 12; m++) {
						int maxDays = new DateTime(y, m, 1, 0, 0).dayOfMonth().getMaximumValue();

						for (int d = 1; d <= maxDays; d++) {
							int maxDayTransactions = (int) ((Math.random() * BoostrapConfiguration.MAX_PER_DAY) + BoostrapConfiguration.MAX_PER_DAY);

							for (int t = 0; t < maxDayTransactions; t++) {
								double amount = Math.random() * (BoostrapConfiguration.MAX_AMOUNT - BoostrapConfiguration.MIN_AMOUNT) + BoostrapConfiguration.MIN_AMOUNT;

								OneOffTransaction transaction = new OneOffTransaction();
								transaction.account = account;
								transaction.label = (amount >= 0) ? "Crédit" : "Débit";
								transaction.amount = amount;
								transaction.date = new DateTime(y, m, d, 0, 0);
								transaction.save();
							}
						}

						if (y == currentYear && m == currentMonth) {
							break;
						}
					}
				}
			}
		}
		Logger.info("  END initOneOffTransactions()");
	}
}
