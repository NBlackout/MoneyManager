package jobs.parsers;

import helpers.jsoup.parsers.accounts.BankAccountParserResult;
import helpers.jsoup.parsers.transactions.BankTransactionParserResult;
import helpers.jsoup.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.websites.IBankWebSiteParser;

import java.util.List;

import models.Account;
import models.transactions.oneoff.OneOffTransaction;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class AccountParser extends Job {

	private Account account;

	private String login;

	private String password;

	public AccountParser(long accountId, String login, String password) {
		this.account = Account.findById(accountId);
		this.login = login;
		this.password = password;
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN AccountCrawler.now()");
		IBankWebSiteParser parser = null;

		switch (account.bank.webSite) {
		case CreditDuNord:
			parser = new CreditDuNordWebSiteParser(login, password);
			break;
		default:
			break;
		}

		List<BankTransactionParserResult> results = parser.retrieveTransactions(account);
		for (BankTransactionParserResult result : results) {
			OneOffTransaction transaction = new OneOffTransaction();
			transaction.account = account;
			transaction.label = result.getLabel();
			transaction.amount = result.getAmount();
			transaction.date = result.getDate();
			transaction.save();
		}

		account.lastSync = DateTime.now();
		account.save();
		Logger.info("  END AccountCrawler.now()");
		return null;
	}
}
