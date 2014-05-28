package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jobs.synchronizers.TransactionsSynchronizer;
import models.Account;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.Category;
import models.transactions.regular.RegularTransaction;

import org.joda.time.DateTime;

public class Accounts extends SuperController {

	public static void index() {
		List<Account> accounts = Account.findAll();

		render(accounts);
	}

	public static void show(Long accountId, DateTime date) {
		if (accountId == null) {
			index();
		}

		DateTime now = DateTime.now();
		int currentYear = now.getYear();
		int currentMonth = now.getMonthOfYear();

		// Date
		date = (date == null) ? now : date;
		List<DateTime> periods = new LinkedList<>();
		for (int year = currentYear - 3; year <= currentYear; year++) {
			for (int month = 1; month <= 12; month++) {
				DateTime period = new DateTime(year, month, 1, 0, 0);
				periods.add(period);

				if (year == date.getYear() && month == date.getMonthOfYear()) {
					date = period;
				}
			}
		}

		// Categories
		List<Category> categories = Category.findAll();

		// Regular transactions
		Map<Category, List<RegularTransaction>> regularTransactions = new HashMap<>();
		for (Category category : categories) {
			regularTransactions.put(category, RegularTransaction.findByAccountIdAndCategoryIdAndDate(accountId, category.id, date));
		}

		// One-off transactions
		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdAndDate(accountId, date);

		render(accountId, periods, date, currentYear, currentMonth, categories, regularTransactions, oneOffTransactions);
	}

	public static void synchronize(long accountId) {
		new TransactionsSynchronizer(accountId).now();

		index();
	}
}
