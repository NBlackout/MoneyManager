package jobs.bootstraps;

import models.Bank;
import models.BankWebSite;
import models.transactions.regular.RegularTransactionCategory;
import models.transactions.regular.RegularTransactionPeriodicity;
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
		Logger.info("  END Bootstrap.doJob()");
	}

	private void initBanks() {
		Logger.info("BEGIN Bootstrap.initBanks()");
		if (Bank.count() == 0) {
			Bank bank = new Bank();
			bank.number = "30076";
			bank.label = "Crédit du Nord";
			bank.webSite = BankWebSite.CreditDuNord;
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
}
