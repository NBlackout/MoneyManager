package helpers.jsoup.data;

import java.util.Map;

import javax.persistence.Entity;

import org.jsoup.Connection.Method;

@Entity
public class LoginAccessData extends AccessData {

	public String loginField;

	public String passwordField;

	public LoginAccessData(String url, Method method, Map<String, String> staticData, String loginField, String passwordField) {
		super(url, method, staticData);

		this.loginField = loginField;
		this.passwordField = passwordField;
	}
}
