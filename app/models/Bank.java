package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Bank extends Model {

	private static final long serialVersionUID = -7683571188654748135L;

	public String label;
}
