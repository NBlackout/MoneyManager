package helpers.jsoup.parsers.websites;

import helpers.jsoup.parsers.accounts.AccountParserResult;
import helpers.jsoup.parsers.transactions.TransactionParserResult;

import java.util.List;

import models.Account;

public interface IWebSiteParser {

	public List<AccountParserResult> retrieveAccounts();

	public List<TransactionParserResult> retrieveTransactions(Account account);

	public void doIt();
}
