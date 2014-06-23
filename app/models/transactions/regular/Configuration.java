package models.transactions.regular;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.Account;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
@Table(name = "`Configuration`")
public class Configuration extends Model {

	private static final long serialVersionUID = -5828603636916049605L;

	@ManyToOne
	public Account account;

	@ManyToOne
	public Category category;

	@ManyToOne
	public Periodicity periodicity;

	public String fixedLabel;

	public String friendlyLabel;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime firstDueDate;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime lastDueDate;

	public boolean active;

	@OneToMany(mappedBy = "configuration")
	public List<RegularTransaction> transactions;
}
