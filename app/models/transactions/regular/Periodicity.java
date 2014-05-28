package models.transactions.regular;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "`Periodicity`")
public class Periodicity extends Model {

	private static final long serialVersionUID = 4239851383172938264L;

	public String label;
}
