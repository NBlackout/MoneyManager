package models.transactions;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class RegularTransactionCategory extends Model {

	private static final long serialVersionUID = 3056650287815629733L;

	public String label;
}
