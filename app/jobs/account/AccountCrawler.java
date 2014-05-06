package jobs.account;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.JsoupParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.F.Promise;

@Every("1mn")
public class AccountCrawler extends Job {

	private static Map<String, String> cookies;

	@Override
	public void doJob() {
		Logger.info("BEGIN AccountCrawler.doJob()");
		crawlAccounts();
		Logger.info("  END AccountCrawler.doJob()");
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN AccountCrawler.now()");
		crawlAccounts();
		Logger.info("  END AccountCrawler.now()");

		return null;
	}

	private void crawlAccounts() {
		Logger.info("BEGIN AccountCrawler.crawlAccounts()");
		if (Account.count() == 0) {
			String url = "https://www.credit-du-nord.fr/saga/authentification";
			Method method = Method.POST;
			String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.16";
			Map<String, String> data = new HashMap<>();
			{
				data.put("pwAuth", "Authentification mot de passe");
				data.put("pagecible", "vos-comptes");
				data.put("bank", "credit-du-nord");
				data.put("username", "");
				data.put("password", "");
			}

			Response response = JsoupConnection.execute(url, method, userAgent, cookies, data);
			if (response != null) {
				cookies = response.cookies();

				try {
					Document document = response.parse();
					List<Map<String, String>> results = JsoupParser.parse(document);
					for (Map<String, String> result : results) {
						Long number = Long.parseLong(result.get("number").replaceAll("\\p{javaSpaceChar}+", ""));
						String label = result.get("label");
						Double balance = Double.parseDouble(result.get("valueEUR").replaceAll("\\p{javaSpaceChar}+", "").replace(",", "."));

						Account account = new Account();
						account.number = number;
						account.label = label;
						account.balance = balance;
						account.save();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Logger.info("  END AccountCrawler.crawlAccounts()");
	}
}
