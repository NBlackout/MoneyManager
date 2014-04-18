package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Account extends Model {

	private static final long serialVersionUID = 8814425930298906224L;

	public Customer customer;

	public Bank bank;

	public String label;

	public double balance;

	public long lastSync;
}
