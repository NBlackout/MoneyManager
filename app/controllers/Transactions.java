package controllers;

import jobs.generators.RegularTransactionGenerator;
import models.Account;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.Category;
import models.transactions.regular.Configuration;
import models.transactions.regular.Periodicity;

import org.joda.time.DateTime;

import play.data.binding.As;

public class Transactions extends SuperController {

	public static void create(Long accountId, Long transactionId, Long categoryId, String label, Double amount, @As("dd/MM/yyyy") DateTime date) {
		if (transactionId != null) {
			OneOffTransaction oneOffTransaction = OneOffTransaction.findById(transactionId);

			label = oneOffTransaction.label;
			amount = oneOffTransaction.amount;
		}

		createConfiguration(accountId, label, amount, categoryId, date);

		Accounts.show(accountId, date.getYear(), date.getMonthOfYear());
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

	private static void createConfiguration(Long accountId, String label, Double amount, Long categoryId, DateTime date) {
		Configuration configuration = new Configuration();
		configuration.account = Account.findById(accountId);
		configuration.label = label;
		configuration.amount = amount;
		configuration.category = Category.findById(categoryId);
		configuration.periodicity = Periodicity.find("byLabel", "Mensuelle").first();
		configuration.firstDueDate = date;
		configuration.active = true;
		configuration.save();

		new RegularTransactionGenerator(configuration.id).now();
	}
}
