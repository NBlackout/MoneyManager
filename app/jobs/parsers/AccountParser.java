package jobs.parsers;

import helpers.jsoup.parsers.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.parsers.websites.IWebSiteParser;
import models.Account;
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
		IWebSiteParser parser = null;

		switch (account.bank.webSite) {
			case CreditDuNord:
				parser = new CreditDuNordWebSiteParser(login, password);
				break;
			default:
				break;
		}

		parser.doIt();
		// List<TransactionParserResult> results = parser.retrieveTransactions(account);
		// for (TransactionParserResult result : results) {
		// OneOffTransaction transaction = OneOffTransaction.find("byLabel", result.getLabel()).first();
		// if (transaction == null) {
		// transaction = new OneOffTransaction();
		// transaction.account = account;
		// transaction.label = result.getLabel();
		// transaction.amount = result.getAmount();
		// transaction.date = result.getDate();
		// transaction.save();
		// }
		// }
		//
		// account.lastSync = DateTime.now();
		// account.save();
		Logger.info("  END AccountCrawler.now()");
		return null;
	}
}
