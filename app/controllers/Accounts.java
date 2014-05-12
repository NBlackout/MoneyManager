package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jobs.parsers.AccountParser;
import models.Account;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.RegularTransaction;
import models.transactions.regular.RegularTransactionCategory;

import org.joda.time.DateTime;

import play.mvc.Controller;

public class Accounts extends Controller {

	public static void index() {
		List<Account> accounts = Account.findAll();

		render(accounts);
	}

	public static void show(Long accountId, Integer year, Integer month) {
		if (accountId == null) {
			index();
		}

		DateTime now = DateTime.now();

		// Year
		int currentYear = now.getYear();
		year = (year == null) ? currentYear : year;
		List<Integer> years = Arrays.asList(currentYear - 3, currentYear - 2, currentYear - 1, currentYear);

		// Month
		int currentMonth = now.getMonthOfYear();
		month = (month == null) ? currentMonth : month;
		List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		// Categories
		List<RegularTransactionCategory> categories = RegularTransactionCategory.findAll();

		// Regular transactions
		Map<RegularTransactionCategory, List<RegularTransaction>> regularTransactions = new HashMap<>();
		for (RegularTransactionCategory category : categories) {
			regularTransactions.put(category, RegularTransaction.findByAccountIdCategoryIdYearMonth(accountId, category.id, year, month));
		}

		// One-off transactions
		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdYearMonth(accountId, year, month);

		render(accountId, currentYear, year, years, currentMonth, month, months, categories, regularTransactions, oneOffTransactions);
	}

	public static void synchronize(long accountId, String login, String password) {
		new AccountParser(accountId, login, password).now();

		index();
	}
}
