package jobs.synchronizers;

import helpers.jsoup.parsers.transactions.TransactionParserResult;
import helpers.jsoup.parsers.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.parsers.websites.IWebSiteParser;

import java.util.Arrays;
import java.util.List;

import models.Account;
import models.transactions.oneoff.OneOffTransaction;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.libs.Crypto;
import play.libs.F.Promise;

public class TransactionsSynchronizer extends Job {

	private Account customAccount;

	public TransactionsSynchronizer() {
		this.customAccount = null;
	}

	public TransactionsSynchronizer(long accountId) {
		this.customAccount = Account.findById(accountId);
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN TransactionsSynchronizer.now()");
		List<Account> accounts = (customAccount != null) ? Arrays.asList(customAccount) : Account.<Account>findAll();
		for (Account account : accounts) {
			synchronize(account);
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

		List<TransactionParserResult> results = parser.retrieveTransactions(account, account.customer.login, Crypto.decryptAES(account.customer.password));
		if (results != null) {
			for (TransactionParserResult result : results) {
				long count = OneOffTransaction.count("byLabelAndAmountAndDate", result.getLabel(), result.getAmount(), result.getDate());
				if (count == 0) {
					OneOffTransaction transaction = new OneOffTransaction();
					transaction.account = account;
					transaction.label = result.getLabel();
					transaction.additionalLabel = result.getAdditionalLabel();
					transaction.amount = result.getAmount();
					transaction.date = result.getDate();
					transaction.save();
				}
			}

			account.lastSync = DateTime.now();
			account.save();
		}
		Logger.info("  END TransactionsSynchronizer.synchronize(" + account.label + ")");
	}
}
