package models;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jsoup.Connection.Method;

import play.db.jpa.Model;

@Entity
public class AccountConfiguration extends Model {

	@OneToOne
	public Account account;

	public String url;

	public Method method;

	@ElementCollection
	public Map<String, String> basicData;
}
