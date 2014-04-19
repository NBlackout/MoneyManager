package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Account extends Model {

	private static final long serialVersionUID = 8814425930298906224L;

	@ManyToOne
	public Customer customer;

	@ManyToOne
	public Bank bank;

	public String label;

	public double balance;

	public long lastSync;
}
