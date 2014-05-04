package jobs.transactions;

import java.util.List;

import models.transactions.regular.RegularTransaction;
import models.transactions.regular.RegularTransactionConfiguration;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.F.Promise;

@Every("5mn")
public class RegularTransactionScheduler extends Job {

	@Override
	public void doJob() {
		Logger.info("BEGIN RegularTransactionScheduler.doJob()");
		generateRegularTransactions();
		Logger.info("  END RegularTransactionScheduler.doJob()");
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN RegularTransactionScheduler.now()");
		generateRegularTransactions();
		Logger.info("  END RegularTransactionScheduler.now()");

		return null;
	}

	private void generateRegularTransactions() {
		Logger.info("BEGIN RegularTransactionScheduler.generateRegularTransactions()");
		List<RegularTransactionConfiguration> configurations = RegularTransactionConfiguration.findAll();
		for (RegularTransactionConfiguration configuration : configurations) {
			DateTime now = DateTime.now();
			DateTime date = configuration.lastDueDate;

			if (configuration.active == true && date.getYear() == now.getYear() && date.getMonthOfYear() == now.getMonthOfYear()) {
				generateRegularTransaction(configuration);
			}
		}
		Logger.info("  END RegularTransactionScheduler.generateRegularTransactions()");
	}

	private void generateRegularTransaction(RegularTransactionConfiguration configuration) {
		Logger.info("BEGIN RegularTransactionScheduler.createRegularTransaction(" + configuration.label + ")");
		DateTime date = configuration.lastDueDate;
		if (date == null) {
			date = configuration.firstDueDate;
		} else {
			switch (configuration.periodicity.label) {
				case "Mensuelle":
					date = date.plusMonths(1);
					break;
				default:
					break;
			}
		}

		RegularTransaction transaction = new RegularTransaction();
		transaction.configuration = configuration;
		transaction.date = date;
		transaction.done = false;
		transaction.save();

		configuration.lastDueDate = transaction.date;
		configuration.save();
		Logger.info("  END RegularTransactionScheduler.createRegularTransaction(" + configuration.label + ")");
	}
}
