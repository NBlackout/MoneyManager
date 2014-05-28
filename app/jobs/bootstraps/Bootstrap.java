package jobs.bootstraps;

import models.Bank;
import models.BankType;
import models.transactions.regular.Category;
import models.transactions.regular.Periodicity;
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
			bank.type = BankType.CreditDuNord;
			bank.save();
		}
		Logger.info("  END Bootstrap.initBanks()");
	}

	private void initPeriodicities() {
		Logger.info("BEGIN Bootstrap.initPeriodicities()");
		if (Periodicity.count() == 0) {
			for (String label : BoostrapConfiguration.PERIODICITY_LABELS) {
				Periodicity periodicity = new Periodicity();
				periodicity.label = label;
				periodicity.save();
			}
		}
		Logger.info("  END Bootstrap.initPeriodicities()");
	}

	private void initCategories() {
		Logger.info("BEGIN Bootstrap.initCategories()");
		if (Category.count() == 0) {
			for (String label : BoostrapConfiguration.CATEGORY_LABELS) {
				Category category = new Category();
				category.label = label;
				category.save();
			}
		}
		Logger.info("  END Bootstrap.initCategories()");
	}
}
