package models.transactions;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
public class RegularTransactionConfiguration extends Transaction {

	private static final long serialVersionUID = -5828603636916049605L;

	@ManyToOne
	public RegularTransactionCategory category;

	@ManyToOne
	public Periodicity periodicity;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime firstDueDate;

	@OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL)
	public List<RegularTransaction> transactions;
}
