package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
@Table(name = "`Account`")
public class Account extends Model {

	private static final long serialVersionUID = 8814425930298906224L;

	@ManyToOne
	public Customer customer;

	public String agency;

	public String rank;

	public String series;

	public String subAccount;

	public String label;

	public double balance;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime lastSync;

	public String getFullNumber() {
		StringBuilder builder = new StringBuilder();
		{
			builder.append(agency);
			builder.append(" ");
			builder.append(rank);
			builder.append(" ");
			builder.append(series);
			builder.append(" ");
			builder.append(subAccount);
		}

		return builder.toString();
	}
}
