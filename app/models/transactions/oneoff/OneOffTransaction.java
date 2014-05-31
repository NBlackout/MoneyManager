package models.transactions.oneoff;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.Account;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
@Table(name = "`OneOffTransaction`")
public class OneOffTransaction extends Model {

	private static final long serialVersionUID = 855382573468954474L;

	@ManyToOne
	public Account account;

	public String label;

	public String additionalLabel;

	public double amount;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime date;

	public static List<OneOffTransaction> findByAccountIdAndYearAndMonth(Long accountId, Integer year, Integer month) {
		if (accountId == null) {
			throw new IllegalArgumentException("accountId cannot be null");
		}
		if (year == null) {
			throw new IllegalArgumentException("year cannot be null");
		}
		if (month == null) {
			throw new IllegalArgumentException("month cannot be null");
		}

		DateTime minDate = new DateTime(year, month, 1, 0, 0);
		DateTime maxDate = minDate.plusMonths(1);

		JPAQuery query = OneOffTransaction.find("account.id = :accountId AND date >= :minDate AND date < :maxDate ORDER BY date DESC, id DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("minDate", minDate);
		query.setParameter("maxDate", maxDate);

		return query.fetch();
	}

	public static OneOffTransaction findByAccountIdAndLabelAndDate(Long accountId, String label, DateTime date) {
		if (accountId == null) {
			throw new IllegalArgumentException("accountId cannot be null");
		}
		if (label == null) {
			throw new IllegalArgumentException("label cannot be null");
		}
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}

		DateTime minDate = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
		DateTime maxDate = minDate.plusMonths(1);

		JPAQuery query = OneOffTransaction.find("account.id = :accountId AND label = :label AND date >= :minDate AND date < :maxDate ORDER BY date DESC, id DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("label", label);
		query.setParameter("minDate", minDate);
		query.setParameter("maxDate", maxDate);

		return query.first();
	}
}
