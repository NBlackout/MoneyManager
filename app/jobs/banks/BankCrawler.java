package jobs.banks;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parser.CreditDuNordParser;
import helpers.jsoup.parser.JsoupParserResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.Account;
import models.Bank;
import models.BankConfiguration;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class BankCrawler extends Job {

	@Override
	public void doJob() {
		Logger.info("BEGIN BankCrawler.doJob()");
		crawlBanks();
		Logger.info("  END BankCrawler.doJob()");
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN BankCrawler.now()");
		crawlBanks();
		Logger.info("  END BankCrawler.now()");
		return null;
	}

	private void crawlBanks() {
		Logger.info("BEGIN BankCrawler.crawlBanks()");
		for (Bank bank : Bank.<Bank>findAll()) {
			crawlBank(bank);
		}
		Logger.info("  END BankCrawler.crawlBanks()");
	}

	private void crawlBank(Bank bank) {
		Logger.info("BEGIN BankCrawler.crawlBank(" + bank.label + ")");
		BankConfiguration configuration = BankConfiguration.find("byBank", bank).first();

		String url = configuration.url;
		Method method = configuration.method;
		String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.16";
		Map<String, String> data = configuration.basicData;
		{
			data.put(configuration.loginField, configuration.loginValue);
			data.put(configuration.passwordField, configuration.passwordValue);
		}

		Response response = JsoupConnection.execute(url, method, userAgent, null, data);
		if (response != null) {
			try {
				Document document = response.parse();

				List<JsoupParserResult> results = new CreditDuNordParser().parse(document);
				for (JsoupParserResult result : results) {
					Account account = new Account();
					account.bank = bank;
					account.number=result.getNumber();
					account.label = result.getLabel();
					account.balance = result.getBalance();
					account.save();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Logger.info("  END BankCrawler.crawlBank(" + bank.label + ")");
	}
}
