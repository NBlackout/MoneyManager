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
		if (transactionId == null) {
			createConfiguration(accountId, label, amount, categoryId, date);
		} else {
			OneOffTransaction oneOffTransaction = OneOffTransaction.findById(transactionId);
			createConfiguration(accountId, oneOffTransaction.label, oneOffTransaction.amount, categoryId, date);
			oneOffTransaction.delete();
		}

		Accounts.show(accountId, null);
	}

	public static void deactivate(Long configurationId) {
		Configuration configuration = Configuration.findById(configurationId);
		configuration.active = false;
		configuration.save();

		Accounts.show(configuration.account.id, null);
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
