package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	private static final long serialVersionUID = -4119779053401224119L;

	public String login;

	public String password;

	public String firstName;

	public String lastName;
}
