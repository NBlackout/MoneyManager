package models.transactions.oneoff;

import java.util.List;

import javax.persistence.Entity;

import models.transactions.Transaction;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
public class OneOffTransaction extends Transaction {

	private static final long serialVersionUID = 855382573468954474L;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime valueDate;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime recordingDate;

	public static List<OneOffTransaction> findByAccountIdAndDate(Long accountId, DateTime date) {
		if (accountId == null) {
			throw new IllegalArgumentException("accountId cannot be null");
		}
		if (date == null) {
			throw new IllegalArgumentException("date cannot be null");
		}

		DateTime minDate = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
		DateTime maxDate = minDate.plusMonths(1);

		JPAQuery query = OneOffTransaction.find("account.id = :accountId AND valueDate >= :minDate AND valueDate < :maxDate ORDER BY valueDate DESC, id DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("minDate", minDate);
		query.setParameter("maxDate", maxDate);

		return query.fetch();
	}
}
