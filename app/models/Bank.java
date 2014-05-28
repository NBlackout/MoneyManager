package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
@Table(name = "`Bank`")
public class Bank extends Model {

	private static final long serialVersionUID = -7683571188654748135L;

	public String number;

	public String label;

	@Enumerated
	public BankType type;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime lastSync;

	@OneToMany(mappedBy = "bank")
	public List<Customer> customers;
}
