package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		if (year == null || month == null) {
			DateTime now = DateTime.now();

			year = now.getYear();
			month = now.getMonthOfYear();
		}

		List<Integer> years = Arrays.asList(year - 3, year - 2, year - 1, year);
		List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		List<RegularTransactionCategory> categories = RegularTransactionCategory.findAll();

		Map<RegularTransactionCategory, List<RegularTransaction>> regularTransactions = new HashMap<>();
		for (RegularTransactionCategory category : categories) {
			regularTransactions.put(category, RegularTransaction.findByAccountIdCategoryIdYearMonth(accountId, category.id, year, month));
		}

		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdYearMonth(accountId, year, month);

		render(accountId, years, year, months, month, categories, regularTransactions, oneOffTransactions);
	}
}
