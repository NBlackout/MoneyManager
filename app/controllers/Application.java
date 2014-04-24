package controllers;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import models.Account;
import models.Transaction;
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
			Calendar calendar = Calendar.getInstance();

			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH) + 1;
		}

		List<Transaction> transactions = Transaction.findByAccountIdYearMonth(accountId, year, month - 1);
		List<Integer> years = buildYearList();
		List<Integer> months = buildMonthList();

		render(transactions, accountId, years, year, months, month);
	}

	private static List<Integer> buildYearList() {
		List<Integer> years = new LinkedList<>();

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
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
