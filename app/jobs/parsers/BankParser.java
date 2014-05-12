package jobs.parsers;

import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.parsers.websites.IWebSiteParser;

import java.util.List;

import models.Account;
import models.Bank;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class BankParser extends Job {

	private Bank bank;

	private String login;

	private String password;

	public BankParser(long bankId, String login, String password) {
		this.bank = Bank.findById(bankId);
		this.login = login;
		this.password = password;
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN BankParser.now()");
		IWebSiteParser parser = null;

		switch (bank.webSite) {
			case CreditDuNord:
				parser = new CreditDuNordWebSiteParser(login, password);
				break;
			default:
				break;
		}

		List<AccountParserResult> results = parser.retrieveAccounts();
		for (AccountParserResult result : results) {
			Account account = Account.find("byLabel", result.getLabel()).first();
			if (account == null) {
				account = new Account();
				account.bank = bank;
				account.number = result.getNumber();
				account.label = result.getLabel();
				account.balance = result.getBalance();
				account.urlNumber = result.getUrlNumber();
				account.save();
			}
		}

		bank.lastSync = DateTime.now();
		bank.save();
		Logger.info("  END BankParser.now()");
		return null;
	}
}
