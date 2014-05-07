package models;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jsoup.Connection.Method;

import play.db.jpa.Model;

@Entity
public class BankConfiguration extends Model {

	@OneToOne
	public Bank bank;

	public String url;

	public Method method;

	@ElementCollection
	public Map<String,String> basicData;

	public String loginField;
	public String loginValue;

	public String passwordField;
	public String passwordValue;
}
