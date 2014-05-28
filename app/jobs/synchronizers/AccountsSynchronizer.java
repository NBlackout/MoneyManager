package jobs.synchronizers;

import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.websites.CreditDuNordWebSiteParser;
import helpers.jsoup.parsers.websites.IWebSiteParser;

import java.util.Arrays;
import java.util.List;

import models.Account;
import models.Bank;
import models.Customer;

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

		for (Customer customer : bank.customers) {
			List<AccountParserResult> results = parser.retrieveAccounts(customer.login, Crypto.decryptAES(customer.password));
			for (AccountParserResult result : results) {
				Account account = Account.find("byLabel", result.getLabel()).first();
				if (account == null) {
					account = new Account();
					account.customer = customer;
					account.agency = result.getAgency();
					account.rank = result.getRank();
					account.series = result.getSeries();
					account.subAccount = result.getSubAccount();
					account.label = result.getLabel();
					account.balance = result.getBalance();
					account.save();
				}
			}
		}

		bank.lastSync = DateTime.now();
		bank.save();
		Logger.info("  END AccountsSynchronizer.synchronize(" + bank.label + ")");
	}
}
