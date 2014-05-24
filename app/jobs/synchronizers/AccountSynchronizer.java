package jobs.synchronizers;

import helpers.jsoup.parsers.transactions.TransactionParserResult;
import helpers.jsoup.parsers.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.parsers.websites.IWebSiteParser;

import java.util.List;

import models.Account;
import models.transactions.oneoff.OneOffTransaction;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.libs.F.Promise;

public class AccountSynchronizer extends Job {

	private Account account;

	private String login;

	private String password;

	public AccountSynchronizer(long accountId, String login, String password) {
		this.account = Account.findById(accountId);
		this.login = login;
		this.password = password;
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN AccountSynchronizer.now()");
		IWebSiteParser parser = null;

		switch (account.bank.webSite) {
			case CreditDuNord:
				parser = new CreditDuNordWebSiteParser(login, password);
				break;
			default:
				break;
		}

		List<TransactionParserResult> results = parser.retrieveTransactions(account);
		for (TransactionParserResult result : results) {
			String label = result.getLabel();
			String additionalLabel = result.getAdditionalLabel();
			double amount = result.getAmount();
			DateTime valueDate = result.getValueDate();
			DateTime recordingDate = result.getRecordingDate();

			long count = OneOffTransaction.count("byLabelAndAmountAndValueDate", label, amount, valueDate);
			if (count == 0) {
				OneOffTransaction transaction = new OneOffTransaction();
				transaction.account = account;
				transaction.label = label;
				transaction.additionalLabel = additionalLabel;
				transaction.amount = amount;
				transaction.valueDate = valueDate;
				transaction.recordingDate = recordingDate;
				transaction.save();
			}
		}

		account.lastSync = DateTime.now();
		account.save();
		Logger.info("  END AccountSynchronizer.now()");
		return null;
	}
}
