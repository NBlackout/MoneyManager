package helpers.jsoup.websites;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parsers.accounts.BankAccountParserResult;
import helpers.jsoup.parsers.accounts.CreditDuNordAccountParser;
import helpers.jsoup.parsers.transactions.BankTransactionParserResult;
import helpers.jsoup.parsers.transactions.CreditDuNordTransactionParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class CreditDuNordWebSiteParser implements IBankWebSiteParser {

	private String login;
	private String password;
	private Map<String, String> cookies;

	public CreditDuNordWebSiteParser(String login, String password) {
		this.login = login;
		this.password = password;
	}

	@Override
	public List<BankAccountParserResult> retrieveAccounts() {
		List<BankAccountParserResult> results = null;

		if (cookies == null) {
			authenticate();
		}

		String url = "https://www.credit-du-nord.fr/vos-comptes/particuliers/transac_tableau_de_bord";
		Method method = Method.GET;
		String userAgent = null;
		Map<String, String> data = null;

		Response response = JsoupConnection.execute(url, method, userAgent, cookies, data);
		if (response != null) {
			try {
				Document document = response.parse();
				results = new CreditDuNordAccountParser().parse(document);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	@Override
	public List<BankTransactionParserResult> retrieveTransactions(Account account) {
		List<BankTransactionParserResult> results = null;

		if (cookies == null) {
			authenticate();
		}

		String url = "https://www.credit-du-nord.fr/vos-comptes/IPT/appmanager/transac/particuliers?_nfpb=true&_windowLabel=T26000359611279636908072&wsrp-urlType=blockingAction";
		Method method = Method.POST;
		String userAgent = null;
		Map<String, String> data = new HashMap<>();
		{
			data.put("execution", "e1s1");
			data.put("_eventId", "clicDetailCompte");
			data.put("idCompteClique", account.urlNumber);
		}

		Response response = JsoupConnection.execute(url, method, userAgent, cookies, data);
		if (response != null) {
			try {
				Document document = response.parse();
				results = new CreditDuNordTransactionParser().parse(document);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	private void authenticate() {
		String url = "https://www.credit-du-nord.fr/saga/authentification";
		Method method = Method.POST;
		String userAgent = "";
		Map<String, String> data = new HashMap<>();
		{
			data.put("pwAuth", "Authentification mot de passe");
			data.put("pagecible", "vos-comptes");
			data.put("bank", "credit-du-nord");
			data.put("username", login);
			data.put("password", password);
		}

		Response response = JsoupConnection.execute(url, method, userAgent, null, data);
		if (response != null) {
			cookies = response.cookies();
		}
	}
}
