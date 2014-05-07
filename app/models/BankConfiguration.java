package models;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jsoup.Connection.Method;

import play.db.jpa.Model;

@Entity
public class BankConfiguration extends Model {

	private static final long serialVersionUID = 1814418457697053181L;

	@OneToOne
	public Bank bank;

	public String url;

	public Method method;

	@ElementCollection
	public Map<String, String> basicData;

	public String loginField;

	public String passwordField;
}
