package controllers;

import jobs.transactions.RegularTransactionScheduler;
import models.Account;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.RegularTransactionCategory;
import models.transactions.regular.RegularTransactionConfiguration;
import models.transactions.regular.RegularTransactionPeriodicity;

import org.joda.time.DateTime;

import play.mvc.Controller;

public class Transactions extends Controller {

	public static void create(Long accountId, Long transactionId, Long categoryId, String label, Double amount, DateTime date) {
		if (transactionId == null) {
			createConfiguration(accountId, label, amount, categoryId, date);
		} else {
			OneOffTransaction oneOffTransaction = OneOffTransaction.findById(transactionId);
			createConfiguration(accountId, oneOffTransaction.label, oneOffTransaction.amount, categoryId, date);
			oneOffTransaction.delete();
		}

		Accounts.show(accountId, null, null);
	}

	public static void deactivate(Long configurationId) {
		RegularTransactionConfiguration configuration = RegularTransactionConfiguration.findById(configurationId);
		configuration.active = false;
		configuration.save();

		Accounts.show(configuration.account.id, null, null);
	}

	private static void createConfiguration(Long accountId, String label, Double amount, Long categoryId, DateTime date) {
		RegularTransactionConfiguration configuration = new RegularTransactionConfiguration();
		configuration.account = Account.findById(accountId);
		configuration.label = label;
		configuration.amount = amount;
		configuration.category = RegularTransactionCategory.findById(categoryId);
		configuration.periodicity = RegularTransactionPeriodicity.find("byLabel", "Mensuelle").first();
		configuration.firstDueDate = date;
		configuration.active = true;
		configuration.save();

		new RegularTransactionScheduler().now();
	}
}
