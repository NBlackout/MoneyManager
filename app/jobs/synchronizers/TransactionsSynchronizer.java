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
import play.libs.Crypto;
import play.libs.F.Promise;

public class TransactionsSynchronizer extends Job {

	private Account account;

	public TransactionsSynchronizer(long accountId) {
		this.account = Account.findById(accountId);
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN AccountSynchronizer.now()");
		IWebSiteParser parser = null;

		switch (account.customer.bank.type) {
			case CreditDuNord:
				parser = new CreditDuNordWebSiteParser();
				break;
			default:
				break;
		}

		List<TransactionParserResult> results = parser.retrieveTransactions(account, account.customer.login, Crypto.decryptAES(account.customer.password));
		for (TransactionParserResult result : results) {
			long count = OneOffTransaction.count("byLabelAndAmountAndValueDate", result.getLabel(), result.getAmount(), result.getValueDate());
			if (count == 0) {
				OneOffTransaction transaction = new OneOffTransaction();
				transaction.account = account;
				transaction.label = result.getLabel();
				transaction.additionalLabel = result.getAdditionalLabel();
				transaction.amount = result.getAmount();
				transaction.valueDate = result.getValueDate();
				transaction.recordingDate = result.getRecordingDate();
				transaction.save();
			}
		}

		account.lastSync = DateTime.now();
		account.save();
		Logger.info("  END AccountSynchronizer.now()");
		return null;
	}
}
