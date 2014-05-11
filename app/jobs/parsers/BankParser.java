package jobs.parsers;

import helpers.jsoup.parsers.accounts.BankAccountParserResult;
import helpers.jsoup.websites.IBankWebSiteParser;
import helpers.jsoup.websites.CreditDuNordWebSiteParser;

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
		IBankWebSiteParser parser = null;

		switch (bank.webSite) {
		case CreditDuNord:
			parser = new CreditDuNordWebSiteParser(login, password);
			break;
		default:
			break;
		}

		List<BankAccountParserResult> results = parser.retrieveAccounts();
		for (BankAccountParserResult result : results) {
			Account account = new Account();
			account.bank = bank;
			account.urlNumber = result.getNumber();
			account.label = result.getLabel();
			account.balance = result.getBalance();
			account.save();
		}

		bank.lastSync = DateTime.now();
		bank.save();
		Logger.info("  END BankParser.now()");
		return null;
	}
}
