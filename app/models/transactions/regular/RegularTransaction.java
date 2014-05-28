package models.transactions.regular;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
@Table(name = "`RegularTransaction`")
public class RegularTransaction extends Model {

	private static final long serialVersionUID = -5828603636916049605L;

	@ManyToOne
	public Configuration configuration;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime date;

	public boolean done;

	public static List<RegularTransaction> findByAccountIdAndCategoryIdAndYearAndMonth(Long accountId, Long categoryId, Integer year, Integer month) {
		if (accountId == null) {
			throw new IllegalArgumentException("accountId cannot be null");
		}
		if (categoryId == null) {
			throw new IllegalArgumentException("categoryId cannot be null");
		}
		if (year == null) {
			throw new IllegalArgumentException("year cannot be null");
		}
		if (month == null) {
			throw new IllegalArgumentException("month cannot be null");
		}

		DateTime minDate = new DateTime(year, month, 1, 0, 0);
		DateTime maxDate = minDate.plusMonths(1);

		JPAQuery query = RegularTransaction.find("configuration.account.id = :accountId AND configuration.category.id = :categoryId AND date >= :minDate AND date < :maxDate ORDER BY date DESC");
		query.setParameter("accountId", accountId);
		query.setParameter("categoryId", categoryId);
		query.setParameter("minDate", minDate);
		query.setParameter("maxDate", maxDate);

		return query.fetch();
	}
}
