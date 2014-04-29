package controllers;

import java.util.Arrays;
import java.util.List;

import models.Account;
import models.transactions.OneOffTransaction;
import models.transactions.Periodicity;
import models.transactions.RegularTransaction;
import models.transactions.RegularTransactionCategory;
import models.transactions.RegularTransactionConfiguration;

import org.joda.time.DateTime;

import play.data.binding.As;
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
		List<RegularTransaction> regularTransactions = RegularTransaction.findByAccountIdYearMonth(accountId, year, month);
		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdYearMonth(accountId, year, month);

		render(accountId, years, year, months, month, categories, regularTransactions, oneOffTransactions);
	}

	public static void createRegularTransaction(Long transactionId, @As("yyyy-MM-dd") DateTime date) {
		if (transactionId == null || date == null) {
			index();
		}

		OneOffTransaction oneOffTransaction = OneOffTransaction.findById(transactionId);
		{
			DateTime dateTime = oneOffTransaction.date;
			
			Account account = oneOffTransaction.account;
			int year = dateTime.getYear();
			int month = dateTime.getMonthOfYear();

			RegularTransactionConfiguration configuration = new RegularTransactionConfiguration();
			{
				configuration.account = account;
				configuration.label = oneOffTransaction.label;
				configuration.amount = oneOffTransaction.amount;
				configuration.periodicity = Periodicity.find("byLabel", "Mensuelle").first();
				configuration.firstDueDate = date;
				configuration.save();
			}

			RegularTransaction regularTransaction = new RegularTransaction();
			{
				regularTransaction.configuration = configuration;
				regularTransaction.date = dateTime;
				regularTransaction.done = true;
				regularTransaction.save();
			}

			oneOffTransaction.delete();

			showAccount(account.id, year, month);
		}
	}

	public static void createRegularTransaction(Long accountId, Integer year, Integer month, String label, Double amount, @As("yyyy-MM-dd") DateTime date) {
		if (accountId == null || year == null || month == null) {
			index();
		}

		RegularTransactionConfiguration configuration = new RegularTransactionConfiguration();
		configuration.account = Account.findById(accountId);
		configuration.label = label;
		configuration.amount = amount;
		configuration.periodicity = Periodicity.find("byLabel", "Mensuelle").first();
		configuration.firstDueDate = date;
		configuration.save();

		RegularTransaction transaction = new RegularTransaction();
		transaction.configuration = configuration;
		transaction.date = date;
		transaction.done = false;
		transaction.save();

		showAccount(accountId, year, month);
	}

	public static void deleteRegularTransaction(Long accountId, Integer year, Integer month, Long configurationId) {
		if (configurationId == null) {
			index();
		}

		RegularTransactionConfiguration configuration = RegularTransactionConfiguration.findById(configurationId);
		configuration.delete();

		showAccount(accountId, year, month);
	}
}
