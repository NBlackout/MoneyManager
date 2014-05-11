package helpers.jsoup.data;

import java.util.Map;

import javax.persistence.Entity;

import org.jsoup.Connection.Method;

@Entity
public class TransactionsAccessData extends AccessData {

	public String accountField;

	public TransactionsAccessData(String url, Method method, Map<String, String> staticData, String accountField) {
		super(url, method, staticData);

		this.accountField = accountField;
	}
}
