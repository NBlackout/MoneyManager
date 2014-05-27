package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Customer extends Model {

	private static final long serialVersionUID = -4119779053401224119L;

	@ManyToOne
	public Bank bank;

	public String login;

	public String password;

	public String firstName;

	public String lastName;

	@OneToMany(mappedBy = "customer")
	public List<Account> accounts;
}
