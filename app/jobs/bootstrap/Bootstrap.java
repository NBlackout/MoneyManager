package jobs.bootstrap;

import models.Account;
import models.Bank;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.RegularTransactionCategory;
import models.transactions.regular.RegularTransactionPeriodicity;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() {
		Logger.info("BEGIN Bootstrap.doJob()");
		initBanks();
		initPeriodicities();
		initCategories();
		// initOneOffTransactions();
		Logger.info("  END Bootstrap.doJob()");
	}

	private void initBanks() {
		Logger.info("BEGIN Bootstrap.initBanks()");
		if (Bank.count() == 0) {
			Bank bank = new Bank();
			bank.label = "Crédit du Nord";
			bank.save();
		}
		Logger.info("  END Bootstrap.initBanks()");
	}

	private void initPeriodicities() {
		Logger.info("BEGIN Bootstrap.initPeriodicities()");
		if (RegularTransactionPeriodicity.count() == 0) {
			for (String label : BoostrapConfiguration.PERIODICITY_LABELS) {
				RegularTransactionPeriodicity regularTransactionPeriodicity = new RegularTransactionPeriodicity();
				regularTransactionPeriodicity.label = label;
				regularTransactionPeriodicity.save();
			}
		}
		Logger.info("  END Bootstrap.initPeriodicities()");
	}

	private void initCategories() {
		Logger.info("BEGIN Bootstrap.initCategories()");
		if (RegularTransactionCategory.count() == 0) {
			for (String label : BoostrapConfiguration.CATEGORY_LABELS) {
				RegularTransactionCategory regularTransactionCategory = new RegularTransactionCategory();
				regularTransactionCategory.label = label;
				regularTransactionCategory.save();
			}
		}
		Logger.info("  END Bootstrap.initCategories()");
	}

	private void initOneOffTransactions() {
		Logger.info("BEGIN Bootstrap.initOneOffTransactions()");
		if (OneOffTransaction.count() == 0) {
			DateTime now = DateTime.now();
			int currentYear = now.getYear();
			int currentMonth = now.getMonthOfYear();

			for (Account account : Account.<Account>findAll()) {
				for (int y = currentYear - BoostrapConfiguration.MAX_YEARS_HISTORY; y <= currentYear; y++) {
					for (int m = 1; m <= 12; m++) {
						int maxDays = new DateTime(y, m, 1, 0, 0).dayOfMonth().getMaximumValue();

						for (int d = 1; d <= maxDays; d++) {
							int maxDayTransactions = (int) (Math.random() * BoostrapConfiguration.MAX_PER_DAY);

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
		Logger.info("  END Bootstrap.initOneOffTransactions()");
	}
}
