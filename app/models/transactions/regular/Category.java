package models.transactions.regular;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "`Category`")
public class Category extends Model {

	private static final long serialVersionUID = 3056650287815629733L;

	public String label;
}
