package models;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Transaction extends Model {

	private static final long serialVersionUID = 2305214514018528308L;

	@ManyToOne
	public Account account;

	public String label;

	public double amount;

	public long date;

	public static List<Transaction> findByAccountIdYearMonth(Long accountId, Integer year, Integer month) {
		if (accountId == null) {
			throw new IllegalArgumentException("accountId cannot be null");
		}
		if (year == null) {
			throw new IllegalArgumentException("year cannot be null");
		}
		if (month == null) {
			throw new IllegalArgumentException("month cannot be null");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		long minDate = calendar.getTimeInMillis();

		calendar.add(Calendar.MONTH, 1);
		long maxDate = calendar.getTimeInMillis();

		JPAQuery query = Transaction.find("account.id = :accountId AND date >= :minDate AND date < :maxDate ORDER BY date DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("minDate", minDate);
		query.setParameter("maxDate", maxDate);

		return query.fetch();
	}
}
