package controllers;

import java.util.LinkedList;
import java.util.List;

import models.Account;
import models.OneOffTransaction;
import models.RegularTransaction;

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

		List<Integer> years = buildYearList();
		List<Integer> months = buildMonthList();
		List<RegularTransaction> regularTransactions = RegularTransaction.findByAccountIdYearMonth(accountId, year, month);
		List<OneOffTransaction> oneOffTransactions = OneOffTransaction.findByAccountIdYearMonth(accountId, year, month);

		render(accountId, years, year, months, month, regularTransactions, oneOffTransactions);
	}

	private static List<Integer> buildYearList() {
		List<Integer> years = new LinkedList<>();

		int currentYear = DateTime.now().getYear();
		for (int year = currentYear - 3; year <= currentYear; year++) {
			years.add(year);
		}

		return years;
	}

	private static List<Integer> buildMonthList() {
		List<Integer> months = new LinkedList<>();

		for (int month = 1; month <= 12; month++) {
			months.add(month);
		}

		return months;
	}
}
