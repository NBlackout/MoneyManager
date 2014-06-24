package controllers;

import jobs.generators.RegularTransactionGenerator;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.Category;
import models.transactions.regular.Configuration;
import models.transactions.regular.Periodicity;
import models.transactions.regular.RegularTransaction;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import play.data.binding.As;

public class Transactions extends SuperController {

	public static void save(Long transactionId, Long categoryId, String fixedLabel, String friendlyLabel, @As("dd/MM/yyyy") DateTime date) {
		OneOffTransaction transaction = OneOffTransaction.findById(transactionId);

		if (categoryId != null && StringUtils.isNotBlank(fixedLabel) && StringUtils.isNotBlank(friendlyLabel) && date != null) {
			Configuration configuration = new Configuration();
			configuration.account = transaction.account;
			configuration.category = Category.findById(categoryId);
			configuration.periodicity = Periodicity.find("byLabel", "Mensuelle").first();
			configuration.fixedLabel = fixedLabel;
			configuration.friendlyLabel = friendlyLabel;
			configuration.firstDueDate = date;
			configuration.lastDueDate = null;
			configuration.active = true;
			configuration.save();

			new RegularTransactionGenerator(configuration.id).now();

			Accounts.show(transaction.account.id, date.getYear(), date.getMonthOfYear());
		} else {
			Accounts.show(transaction.account.id, null, null);
		}
	}

	public static void toggle(Long configurationId, Integer year, Integer month) {
		Configuration configuration = Configuration.findById(configurationId);
		configuration.active = (configuration.active == false);
		configuration.save();

		Accounts.show(configuration.account.id, year, month);
	}

	public static void delete(Long configurationId, Integer year, Integer month) {
		Configuration configuration = Configuration.findById(configurationId);

		for (RegularTransaction transaction : RegularTransaction.find("byConfiguration", configuration).<RegularTransaction>fetch()) {
			transaction.delete();
		}

		configuration.delete();

		Accounts.show(configuration.account.id, year, month);
	}

	public static void generate(Long configurationId) {
		new RegularTransactionGenerator(configurationId).now();

		Configuration configuration = Configuration.findById(configurationId);
		DateTime date = configuration.lastDueDate;

		Accounts.show(configuration.account.id, date.getYear(), date.getMonthOfYear());
	}
}
