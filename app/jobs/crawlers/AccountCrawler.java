package jobs.crawlers;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parsers.transactions.CreditDuNordTransactionParser;
import helpers.jsoup.parsers.transactions.TransactionParserResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.Account;
import models.AccountConfiguration;
import models.BankConfiguration;
import models.transactions.oneoff.OneOffTransaction;

import org.joda.time.DateTime;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class AccountCrawler  extends Job {

	private Account account;

	public AccountCrawler(long accountId) {
		this.account = Account.findById(accountId);
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN AccountCrawler.now()");
		AccountConfiguration accountConf = AccountConfiguration.find("byAccount", account).first();
		BankConfiguration bankConf = BankConfiguration.find("byBank", account.bank).first();

		String url = accountConf.url;
		Method method = accountConf.method;
		String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.16";
		Map<String, String> cookies = bankConf.cookies;
		Map<String, String> data = accountConf.basicData;

		Response response = JsoupConnection.execute(url, method, userAgent, cookies, data);
		if (response != null) {
			try {
				Document document = response.parse();

				List<TransactionParserResult> results = new CreditDuNordTransactionParser().parse(document);
				for (TransactionParserResult  result : results) {
					OneOffTransaction transaction= new OneOffTransaction();
					transaction.account = account;
					transaction.label = result.getLabel();
					transaction.amount = result.getAmount();
					transaction.date = result.getDate();
					transaction.save();
				}

				account.lastSync = DateTime.now();
				account.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Logger.info("  END AccountCrawler.now()");
		return null;
	}
}
