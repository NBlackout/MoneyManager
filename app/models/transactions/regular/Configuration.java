package models.transactions.regular;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.transactions.Transaction;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "`Configuration`")
public class Configuration extends Transaction {

	private static final long serialVersionUID = -5828603636916049605L;

	@ManyToOne
	public Category category;

	@ManyToOne
	public Periodicity periodicity;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime firstDueDate;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime lastDueDate;

	public boolean active;

	@OneToMany(mappedBy = "configuration")
	public List<RegularTransaction> transactions;
}
