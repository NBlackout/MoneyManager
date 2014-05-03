package models.transactions.regular;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class RegularTransactionPeriodicity extends Model {

	private static final long serialVersionUID = 4239851383172938264L;

	public String label;
}
