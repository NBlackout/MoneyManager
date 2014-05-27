package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	private static final long serialVersionUID = -8294500956579299894L;

	public String login;

	public String password;

	public String locale;
}
