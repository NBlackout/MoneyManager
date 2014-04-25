package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
public class Transaction extends Model {

	private static final long serialVersionUID = 2305214514018528308L;

	@ManyToOne
	public Account account;

	public String label;

	public double amount;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime dateTime;

	public boolean monthly;

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

		DateTime minDateTime = new DateTime(year, month, 1, 0, 0);
		DateTime maxDateTime = minDateTime.plusMonths(1);

		JPAQuery query = Transaction.find("account.id = :accountId AND dateTime >= :minDateTime AND dateTime < :maxDateTime ORDER BY dateTime DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("minDateTime", minDateTime);
		query.setParameter("maxDateTime", maxDateTime);

		return query.fetch();
	}
}
