package helpers.jsoup.data;

import java.util.Map;

import javax.persistence.Entity;

import org.jsoup.Connection.Method;

@Entity
public class AccountsAccessData extends AccessData {

	public AccountsAccessData(String url, Method method, Map<String, String> staticData) {
		super(url, method, staticData);
	}
}
