package jobs.crawlers;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.accounts.CreditDuNordAccountParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Account;
import models.AccountConfiguration;
import models.Bank;
import models.BankConfiguration;

import org.joda.time.DateTime;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class BankCrawler extends Job {

	private Bank bank;

	private String login;

	private String password;

	public BankCrawler(long bankId, String login, String password) {
		this.bank = Bank.findById(bankId);
		this.login = login;
		this.password = password;
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN BankCrawler.now()");
		BankConfiguration bankConf = BankConfiguration.find("byBank", bank).first();

		String url = bankConf.url;
		Method method = bankConf.method;
		String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.16";
		Map<String, String> data = bankConf.basicData;
		{
			data.put(bankConf.loginField, login);
			data.put(bankConf.passwordField, password);
		}

		Response response = JsoupConnection.execute(url, method, userAgent, null, data);
		if (response != null) {
			bankConf.cookies = response.cookies();
			bankConf.save();

			try {
				Document document = response.parse();

				List<AccountParserResult> results = new CreditDuNordAccountParser().parse(document);
				for (AccountParserResult result : results) {
					Account account = new Account();
					account.bank = bank;
					account.number = result.getNumber();
					account.label = result.getLabel();
					account.balance = result.getBalance();
					account.save();

					AccountConfiguration accountConf= new AccountConfiguration();
					accountConf.account=account;
					accountConf.url="https://www.credit-du-nord.fr/vos-comptes/IPT/appmanager/transac/particuliers?_nfpb=true&_windowLabel=T26000359611279636908072&wsrp-urlType=blockingAction";
					accountConf.method=Method.POST;
					accountConf.basicData = new HashMap<>(); {
						accountConf.basicData.put("idCompteClique", account.number + "_EUR_P00301");
						accountConf.basicData.put("execution", "e1s1");
						accountConf.basicData.put("_eventId", "clicDetailCompte");
					}
					accountConf.save();
				}

				bank.lastSync = DateTime.now();
				bank.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Logger.info("  END BankCrawler.now()");
		return null;
	}
}
