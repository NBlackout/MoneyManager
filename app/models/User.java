package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "`User`")
public class User extends Model {

	private static final long serialVersionUID = -8294500956579299894L;

	public String login;

	public String password;

	public String locale;
}
