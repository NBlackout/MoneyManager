package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Customer extends Model {

	private static final long serialVersionUID = -4119779053401224119L;

	public String firstName;

	public String lastName;
}
