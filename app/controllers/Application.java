package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jobs.transactions.RegularTransactionScheduler;
import models.Account;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.RegularTransaction;
import models.transactions.regular.RegularTransactionCategory;
import models.transactions.regular.RegularTransactionConfiguration;
import models.transactions.regular.RegularTransactionPeriodicity;

import org.joda.time.DateTime;

import play.mvc.Controller;

public class Application extends Controller {

	public static void index() {
		List<Account> accounts = Account.findAll();

		render(accounts);
	}

	public static void showAccount(Long accountId, Integer year, Integer month) {
		if (accountId == null) {
			index();
		}

		if (year == null || month == null) {
			DateTime now = DateTime.now();

			year = now.getYear();
			month = now.getMonthOfYear();
		}

		List<Integer> years = Arrays.asList(year - 3, year - 2, year - 1, year);
		List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		List<RegularTransactionCategory> categories = RegularTransactionCategory.findAll();

		Map<String, List<RegularTransaction>> regularTransactions = new HashMap<>();
		for (RegularTransactionCategory category : categories) {
			regularTransactions.put(category.label, RegularTransaction.findByAccountIdCategoryIdYearMonth(accountId, category.id, year, month));
		}

		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdYearMonth(accountId, year, month);

		render(accountId, years, year, months, month, categories, regularTransactions, oneOffTransactions);
	}

	public static void createRegularTransaction(Long accountId, Long transactionId, Long categoryId, String label, Double amount, DateTime date) {
		if (accountId == null || categoryId == null || date == null) {
			index();
		}

		if (transactionId == null) {
			createConfiguration(accountId, label, amount, categoryId, date);
		} else {
			OneOffTransaction oneOffTransaction = OneOffTransaction.findById(transactionId);
			createConfiguration(accountId, oneOffTransaction.label, oneOffTransaction.amount, categoryId, date);
			oneOffTransaction.delete();
		}

		showAccount(accountId, null, null);
	}

	public static void deactivateRegularTransaction(Long configurationId) {
		if (configurationId == null) {
			index();
		}

		RegularTransactionConfiguration configuration = RegularTransactionConfiguration.findById(configurationId);
		configuration.active = false;
		configuration.save();

		showAccount(configuration.account.id, null, null);
	}

	private static void createConfiguration(Long accountId, String label, Double amount, Long categoryId, DateTime date) {
		RegularTransactionConfiguration configuration = new RegularTransactionConfiguration();
		configuration.account = Account.findById(accountId);
		configuration.label = label;
		configuration.amount = amount;
		configuration.category = RegularTransactionCategory.findById(categoryId);
		configuration.periodicity = RegularTransactionPeriodicity.find("byLabel", "Mensuelle").first();
		configuration.firstDueDate = date;
		configuration.lastDueDate = date;
		configuration.active = true;
		configuration.save();

		new RegularTransactionScheduler().now();
	}
}
