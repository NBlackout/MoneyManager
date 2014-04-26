package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
public class RegularTransactionConfiguration extends Transaction {

	private static final long serialVersionUID = -5828603636916049605L;

	@ManyToOne
	public Periodicity periodicity;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime firstDueDate;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime lastDueDate;
}
