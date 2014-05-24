package models.transactions;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import models.Account;
import play.db.jpa.Model;

@MappedSuperclass
public class Transaction extends Model {

	private static final long serialVersionUID = 2305214514018528308L;

	@ManyToOne
	public Account account;

	public String label;

	public String additionalLabel;

	public double amount;
}
