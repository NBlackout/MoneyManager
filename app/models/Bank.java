package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Bank extends Model {

	private static final long serialVersionUID = -7683571188654748135L;

	public String label;

	@OneToMany(mappedBy = "bank")
	public List<Account> accounts;
}
