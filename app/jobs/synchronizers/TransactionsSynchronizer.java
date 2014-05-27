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
import play.jobs.On;
import play.libs.Crypto;
import play.libs.F.Promise;

@On("0 0 0 * * ?")
public class TransactionsSynchronizer extends Job {

	private Account customAccount;

	public TransactionsSynchronizer(long accountId) {
		this.customAccount = Account.findById(accountId);
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN TransactionsSynchronizer.now()");
		if (customAccount != null) {
			synchronize(customAccount);
		} else {
			for (Account account : Account.<Account>findAll()) {
				synchronize(account);
			}
		}
		Logger.info("  END TransactionsSynchronizer.now()");
		return null;
	}

	private void synchronize(Account account) {
		Logger.info("BEGIN TransactionsSynchronizer.synchronize(" + account.label + ")");
		IWebSiteParser parser = null;

		switch (account.customer.bank.type) {
			case CreditDuNord:
				parser = new CreditDuNordWebSiteParser();
				break;
			default:
				break;
		}

		List<TransactionParserResult> results = parser.retrieveTransactions(customAccount, customAccount.customer.login, Crypto.decryptAES(customAccount.customer.password));
		for (TransactionParserResult result : results) {
			long count = OneOffTransaction.count("byLabelAndAmountAndValueDate", result.getLabel(), result.getAmount(), result.getValueDate());
			if (count == 0) {
				OneOffTransaction transaction = new OneOffTransaction();
				transaction.account = customAccount;
				transaction.label = result.getLabel();
				transaction.additionalLabel = result.getAdditionalLabel();
				transaction.amount = result.getAmount();
				transaction.valueDate = result.getValueDate();
				transaction.recordingDate = result.getRecordingDate();
				transaction.save();
			}
		}

		customAccount.lastSync = DateTime.now();
		customAccount.save();
		Logger.info("  END TransactionsSynchronizer.synchronize(" + account.label + ")");
	}
}
