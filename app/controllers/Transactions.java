package controllers;

import jobs.generators.RegularTransactionGenerator;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.Category;
import models.transactions.regular.Configuration;
import models.transactions.regular.Periodicity;

import org.joda.time.DateTime;

import play.data.binding.As;

public class Transactions extends SuperController {

	public static void create(Long transactionId, Long categoryId, String friendlyLabel, @As("dd/MM/yyyy") DateTime date) {
		OneOffTransaction transaction = OneOffTransaction.findById(transactionId);

		Configuration configuration = new Configuration();
		configuration.account = transaction.account;
		configuration.category = Category.findById(categoryId);
		configuration.periodicity = Periodicity.find("byLabel", "Mensuelle").first();
		configuration.label = transaction.label;
		configuration.friendlyLabel = friendlyLabel;
		configuration.firstDueDate = date;
		configuration.lastDueDate = null;
		configuration.active = true;
		configuration.save();

		new RegularTransactionGenerator(configuration.id).now();

		Accounts.show(transaction.account.id, date.getYear(), date.getMonthOfYear());
	}

	public static void deactivate(Long configurationId) {
		Configuration configuration = Configuration.findById(configurationId);
		configuration.active = false;
		configuration.save();

		Accounts.show(configuration.account.id, null, null);
	}

	public static void generate(Long configurationId) {
		new RegularTransactionGenerator(configurationId).now();

		Configuration configuration = Configuration.findById(configurationId);
		DateTime date = configuration.lastDueDate;

		Accounts.show(configuration.account.id, date.getYear(), date.getMonthOfYear());
	}
}
