package jobs.generators;

import java.util.Arrays;
import java.util.List;

import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.Configuration;
import models.transactions.regular.RegularTransaction;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class RegularTransactionGenerator extends Job {

	private Configuration customConfiguration;

	public RegularTransactionGenerator() {
		this.customConfiguration = null;
	}

	public RegularTransactionGenerator(long configurationId) {
		this.customConfiguration = Configuration.findById(configurationId);
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN RegularTransactionGenerator.now()");
		List<Configuration> configurations = (customConfiguration != null) ? Arrays.asList(customConfiguration) : Configuration.<Configuration>findAll();
		for (Configuration configuration : configurations) {
			if (configuration.active == true) {
				DateTime date = configuration.lastDueDate;
				if (date == null || date.isBefore(DateTime.now()) == true) {
					generate(configuration);
				}
			}
		}
		Logger.info("  END RegularTransactionGenerator.now()");

		return null;
	}

	private void generate(Configuration configuration) {
		Logger.info("BEGIN RegularTransactionGenerator.generate(" + configuration.friendlyLabel + ")");
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

		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdAndLabelAndDate(configuration.account.id, configuration.fixedLabel, date);
		if (oneOffTransactions != null && oneOffTransactions.isEmpty() == false) {
			for (OneOffTransaction oneOffTransaction : oneOffTransactions) {
				RegularTransaction transaction = new RegularTransaction();
				transaction.configuration = configuration;
				transaction.expectedDate = date;
				transaction.oneOffTransaction = oneOffTransaction;
				transaction.save();

				if (configuration.lastDueDate == null || configuration.lastDueDate.isBefore(oneOffTransaction.date) == true) {
					configuration.lastDueDate = oneOffTransaction.date;
					configuration.save();
				}
			}
		} else {
			RegularTransaction transaction = new RegularTransaction();
			transaction.configuration = configuration;
			transaction.expectedDate = date;
			transaction.oneOffTransaction = null;
			transaction.save();

			configuration.lastDueDate = date;
			configuration.save();
		}

		Logger.info("  END RegularTransactionGenerator.generate(" + configuration.friendlyLabel + ")");
	}
}
