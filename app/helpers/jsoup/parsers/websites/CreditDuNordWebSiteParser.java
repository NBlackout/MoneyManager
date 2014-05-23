package helpers.jsoup.parsers.websites;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.accounts.CreditDuNordAccountParser;
import helpers.jsoup.parsers.transactions.CreditDuNordTransactionParser;
import helpers.jsoup.parsers.transactions.TransactionParserResult;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class CreditDuNordWebSiteParser implements IWebSiteParser {

	private String login;

	private String password;

	private Map<String, String> cookies;

	public CreditDuNordWebSiteParser(String login, String password) {
		this.login = login;
		this.password = password;
	}

	@Override
	public List<AccountParserResult> retrieveAccounts() {
		List<AccountParserResult> results = null;

		if (cookies == null) {
			authenticate();
		}

		String url = "https://www.credit-du-nord.fr/vos-comptes/particuliers";
		// Method method = Method.GET;
		// String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.17";
		// Map<String, String> data = null;

		Response response = JsoupConnection.get(url, cookies);
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
	public List<TransactionParserResult> retrieveTransactions(Account account) {
		List<TransactionParserResult> results = null;

		if (cookies == null) {
			authenticate();
		}

		String url = "https://www.credit-du-nord.fr/vos-comptes/IPT/appmanager/transac/particuliers?_nfpb=true&_windowLabel=T26000359611279636908072&wsrp-urlType=blockingAction";
		Map<String, String> data = new HashMap<>();
		{
			data.put("execution", "e1s1");
			data.put("_eventId", "clicDetailCompte");
			data.put("idCompteClique", account.urlNumber);
		}

		Response response = JsoupConnection.post(url, cookies, data);
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

	@Override
	public void doIt() {
		if (cookies == null) {
			authenticate();
		}

		String url1 = "https://www.credit-du-nord.fr/vos-comptes/particuliers/V1_transactional_portal_page_26";
		Response response1 = JsoupConnection.get(url1, cookies);
		if (response1 != null) {
			String url2 = "https://www.credit-du-nord.fr/vos-comptes/IPT/appmanager/transac/particuliers?_cdnRemUrl=%2FtransacClippe%2FTEL_2.asp&_cdnPtlKey=MTIyNDA%3D";
			Map<String, String> data2 = new HashMap<>();
			{
				data2.put("urlPROXYTCH", "/vos-comptes/IPT/appmanager/transac/particuliers");
				data2.put("box1", "on");
				data2.put("banque1", "30076");
				data2.put("agence1", "02455");
				data2.put("classement1", "211728");
				data2.put("serie1", "003");
				data2.put("sscompte1", "00");
				data2.put("devise1", "EUR");
				data2.put("deviseCCB1", "0131");
				data2.put("nbCompte", "2");
				data2.put("ChoixDate", "Autres");
				data2.put("JourDebut", "01");
				data2.put("MoisDebut", "01");
				data2.put("AnDebut", "2014");
				data2.put("JourFin", "23");
				data2.put("MoisFin", "05");
				data2.put("AnFin", "2014");
				data2.put("logiciel", "TXT");
			}

			Response response2 = JsoupConnection.post(url2, cookies, data2);
			if (response2 != null) {
				String url3 = "https://www.credit-du-nord.fr/vos-comptes/IPT/cdnProxyResource/transacClippe/TCH_Envoi_1.asp";

				Response response3 = JsoupConnection.get(url3, cookies);
				if (response3 != null) {
					byte[] bytes = response3.bodyAsBytes();
					ByteArrayInputStream input = new ByteArrayInputStream(bytes);

					try (InputStreamReader isr = new InputStreamReader(input); BufferedReader reader = new BufferedReader(isr)) {
						String line = reader.readLine();
						while ((line = reader.readLine()) != null) {
							String[] split = line.split("\t");
							System.out.println(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void authenticate() {
		String url = "https://www.credit-du-nord.fr/saga/authentification";
		Map<String, String> data = new HashMap<>();
		{
			data.put("bank", "credit-du-nord");
			data.put("username", login);
			data.put("password", password);
		}

		Response response = JsoupConnection.post(url, null, data);
		if (response != null) {
			cookies = response.cookies();
		}
	}
}
