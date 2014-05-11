package helpers.jsoup.websites;

import helpers.jsoup.parsers.accounts.BankAccountParserResult;
import helpers.jsoup.parsers.transactions.BankTransactionParserResult;

import java.util.List;

import models.Account;

public interface IBankWebSiteParser {

	public List<BankAccountParserResult> retrieveAccounts();

	public List<BankTransactionParserResult> retrieveTransactions(Account account);
}
