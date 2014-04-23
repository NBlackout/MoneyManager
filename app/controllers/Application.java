package controllers;

import java.util.Calendar;
import java.util.List;

import models.Account;
import models.Transaction;
import play.db.jpa.GenericModel.JPAQuery;
import play.mvc.Controller;

public class Application extends Controller {

	public static final int PAGE_SIZE = 10;

	public static void index() {
		List<Account> accounts = Account.findAll();

		render(accounts);
	}

	public static void showAccount(long accountId, int page) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
		long minDate = calendar.getTimeInMillis();

		calendar.add(Calendar.MONTH, 1);
		long maxDate = calendar.getTimeInMillis();

		JPAQuery query = Transaction.find("account.id = :accountId AND date >= :minDate AND date < :maxDate ORDER BY date DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("minDate", minDate);
		query.setParameter("maxDate", maxDate);

		List<Transaction> transactions = query.fetch(page, PAGE_SIZE);

		render(transactions);
	}
}
