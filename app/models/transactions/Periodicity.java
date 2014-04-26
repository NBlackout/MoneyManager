package models.transactions;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Periodicity extends Model {

	private static final long serialVersionUID = 4239851383172938264L;

	public String label;
}
