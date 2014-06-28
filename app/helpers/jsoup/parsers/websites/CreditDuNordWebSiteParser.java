package helpers.jsoup.parsers.websites;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.accounts.CreditDuNordAccountParser;
import helpers.jsoup.parsers.accounts.IAccountParser;
import helpers.jsoup.parsers.transactions.CreditDuNordTransactionParser;
import helpers.jsoup.parsers.transactions.ITransactionParser;
import helpers.jsoup.parsers.transactions.TransactionParserResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;

import org.joda.time.DateTime;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class CreditDuNordWebSiteParser implements IWebSiteParser {

	private Map<String, String> cookies;

	@Override
	public List<AccountParserResult> retrieveAccounts(String login, String password) {
		List<AccountParserResult> results = null;

		cookies = getAuthenticationCookies(login, password);
		String url = "https://www.credit-du-nord.fr/vos-comptes/particuliers";

		Response response = JsoupConnection.get(url, cookies);
		if (response != null) {
			try {
				Document document = response.parse();

				IAccountParser parser = new CreditDuNordAccountParser();
				results = parser.parse(document);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return results;
	}

	@Override
	public List<TransactionParserResult> retrieveTransactions(Account account, String login, String password) {
		List<TransactionParserResult> results = null;

		if (cookies == null) {
			cookies = getAuthenticationCookies(login, password);
		}

		String url1 = "https://www.credit-du-nord.fr/vos-comptes/particuliers/V1_transactional_portal_page_26";

		Response response1 = JsoupConnection.get(url1, cookies);
		if (response1 != null) {
			DateTime end = DateTime.now();
			DateTime begin = (account.lastSync != null) ? end.minusMonths(2) : new DateTime(2000, 1, 1, 0, 0);

			String url2 = "https://www.credit-du-nord.fr/vos-comptes/IPT/appmanager/transac/particuliers?_cdnRemUrl=%2FtransacClippe%2FTEL_2.asp&_cdnPtlKey=MTIyNDA%3D";
			Map<String, String> data2 = new HashMap<>();
			{
				data2.put("urlPROXYTCH", "/vos-comptes/IPT/appmanager/transac/particuliers");
				data2.put("box1", "on");
				data2.put("banque1", account.customer.bank.number);
				data2.put("agence1", account.agency);
				data2.put("classement1", account.rank);
				data2.put("serie1", account.series);
				data2.put("sscompte1", account.subAccount);
				data2.put("devise1", "EUR");
				data2.put("deviseCCB1", "0131");
				data2.put("nbCompte", Integer.toString(account.customer.accounts.size()));
				data2.put("ChoixDate", "Autres");
				data2.put("JourDebut", Integer.toString(begin.getDayOfMonth()));
				data2.put("MoisDebut", Integer.toString(begin.getMonthOfYear()));
				data2.put("AnDebut", Integer.toString(begin.getYear()));
				data2.put("JourFin", Integer.toString(end.getDayOfMonth()));
				data2.put("MoisFin", Integer.toString(end.getMonthOfYear() + 1));
				data2.put("AnFin", Integer.toString(end.getYear()));
				data2.put("logiciel", "TXT");
			}

			Response response2 = JsoupConnection.post(url2, cookies, data2);
			if (response2 != null) {
				String url3 = "https://www.credit-du-nord.fr/vos-comptes/IPT/cdnProxyResource/transacClippe/TCH_Envoi_1.asp";

				Response response3 = JsoupConnection.get(url3, cookies);
				if (response3 != null) {
					byte[] bytes = response3.bodyAsBytes();
					ByteArrayInputStream input = new ByteArrayInputStream(bytes);

					ITransactionParser parser = new CreditDuNordTransactionParser();
					results = parser.parse(input);
				}
			}
		}

		return results;
	}

	private Map<String, String> getAuthenticationCookies(String login, String password) {
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

		return cookies;
	}
}
