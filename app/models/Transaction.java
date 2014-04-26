package models;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.db.jpa.Model;

@MappedSuperclass
public class Transaction extends Model {

	private static final long serialVersionUID = 2305214514018528308L;

	@ManyToOne
	public Account account;

	public String label;

	public double amount;
}
