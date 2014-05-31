package jobs.synchronizers;

import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.transactions.TransactionParserResult;
import helpers.jsoup.parsers.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.parsers.websites.IWebSiteParser;

import java.util.Arrays;
import java.util.List;

import models.Account;
import models.Bank;
import models.Customer;
import models.transactions.oneoff.OneOffTransaction;

import org.joda.time.DateTime;

import play.Logger;
import play.jobs.Job;
import play.libs.Crypto;
import play.libs.F.Promise;

public class AccountsSynchronizer extends Job {

	private Bank customBank;

	public AccountsSynchronizer() {
		this.customBank = null;
	}

	public AccountsSynchronizer(long bankId) {
		this.customBank = Bank.findById(bankId);
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN AccountsSynchronizer.now()");
		List<Bank> banks = (customBank != null) ? Arrays.asList(customBank) : Bank.<Bank>findAll();
		for (Bank bank : banks) {
			synchronize(bank);
		}
		Logger.info("  END AccountsSynchronizer.now()");

		return null;
	}

	private void synchronize(Bank bank) {
		Logger.info("BEGIN AccountsSynchronizer.synchronize(" + bank.label + ")");
		IWebSiteParser parser = null;

		switch (bank.type) {
			case CreditDuNord:
				parser = new CreditDuNordWebSiteParser();
				break;
			default:
				break;
		}

		if (parser != null) {
			for (Customer customer : bank.customers) {
				String login = customer.login;
				String password = Crypto.decryptAES(customer.password);

				List<AccountParserResult> aResults = parser.retrieveAccounts(login, password);
				if (aResults != null) {
					for (AccountParserResult aResult : aResults) {
						// Retrieve account data
						Account account = Account.find("byLabel", aResult.getLabel()).first();
						if (account == null) {
							account = new Account();
							account.customer = customer;
							account.agency = aResult.getAgency();
							account.rank = aResult.getRank();
							account.series = aResult.getSeries();
							account.subAccount = aResult.getSubAccount();
							account.label = aResult.getLabel();
							account.balance = aResult.getBalance();
							account.save();
						}

						// Retrieve transactions data
						List<TransactionParserResult> tResults = parser.retrieveTransactions(account, login, password);
						if (tResults != null) {
							for (TransactionParserResult tResult : tResults) {
								long count = OneOffTransaction.count("byLabelAndAmountAndDate", tResult.getLabel(), tResult.getAmount(), tResult.getDate());
								if (count == 0) {
									OneOffTransaction transaction = new OneOffTransaction();
									transaction.account = account;
									transaction.label = tResult.getLabel();
									transaction.additionalLabel = tResult.getAdditionalLabel();
									transaction.amount = tResult.getAmount();
									transaction.date = tResult.getDate();
									transaction.save();
								}
							}

							account.lastSync = DateTime.now();
							account.save();
						}
					}

					bank.lastSync = DateTime.now();
					bank.save();
				}
			}
		}
		Logger.info("  END AccountsSynchronizer.synchronize(" + bank.label + ")");
	}
}
