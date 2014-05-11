//package models;
//
//import helpers.jsoup.JsoupConnection;
//import helpers.jsoup.data.AccountsAccessData;
//import helpers.jsoup.data.LoginAccessData;
//import helpers.jsoup.data.TransactionsAccessData;
//import helpers.jsoup.parsers.accounts.BankAccountParser;
//import helpers.jsoup.parsers.accounts.BankAccountParserResult;
//import helpers.jsoup.parsers.accounts.CreditDuNordAccountParser;
//import helpers.jsoup.parsers.transactions.CreditDuNordTransactionParser;
//import helpers.jsoup.parsers.transactions.BankTransactionParser;
//import helpers.jsoup.parsers.transactions.BankTransactionParserResult;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.Entity;
//import javax.persistence.OneToOne;
//
//import models.transactions.oneoff.OneOffTransaction;
//
//import org.joda.time.DateTime;
//import org.jsoup.Connection.Method;
//import org.jsoup.Connection.Response;
//import org.jsoup.nodes.Document;
//
//import play.db.jpa.Model;
//
//@Entity
//public class BankConfiguration extends Model {
//
//	private static final long serialVersionUID = 1814418457697053181L;
//
//	private Map<String, String> cookies;
//
//	@OneToOne
//	public Bank bank;
//
//	@OneToOne
//	public LoginAccessData loginAccessData;
//
//	@OneToOne
//	public AccountsAccessData accountsAccessData;
//
//	@OneToOne
//	public TransactionsAccessData transactionsAccessData;
//
//	public void login(String login, String password) {
//		String url = loginAccessData.url;
//		Method method = loginAccessData.method;
//		String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.17";
//		Map<String, String> data = new HashMap<>(loginAccessData.staticData);
//		data.put(loginAccessData.loginField, login);
//		data.put(loginAccessData.passwordField, password);
//
//		Response response = JsoupConnection.execute(url, method, userAgent, null, data);
//		if (response != null) {
//			cookies = response.cookies();
//		}
//	}
//
//	public void updateAccounts(String login, String password) {
//		if (cookies == null) {
//			login(login, password);
//		}
//
//		String url = accountsAccessData.url;
//		Method method = accountsAccessData.method;
//		String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.17";
//		Map<String, String> data = new HashMap<>(accountsAccessData.staticData);
//
//		Response response = JsoupConnection.execute(url, method, userAgent, null, data);
//		if (response != null) {
//			try {
//				Document document = response.parse();
//
//				List<BankAccountParserResult> results = new CreditDuNordAccountParser().parse(document);
//				for (BankAccountParserResult result : results) {
//					Account account = new Account();
//					account.bank = bank;
//					account.number = result.getNumber();
//					account.label = result.getLabel();
//					account.balance = result.getBalance();
//					account.urlNumber = result.getUrlNumber();
//					account.save();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void updateTransactions(String login, String password, Account account) {
//		if (cookies == null) {
//			login(login, password);
//		}
//
//		String url = transactionsAccessData.url;
//		Method method = transactionsAccessData.method;
//		String userAgent = "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.17";
//		Map<String, String> data = new HashMap<>(transactionsAccessData.staticData);
//		data.put(transactionsAccessData.accountField, account.urlNumber);
//
//		Response response = JsoupConnection.execute(url, method, userAgent, cookies, data);
//		if (response != null) {
//			try {
//				Document document = response.parse();
//
//				List<BankTransactionParserResult> results = new CreditDuNordTransactionParser().parse(document);
//				for (BankTransactionParserResult result : results) {
//					OneOffTransaction transaction = new OneOffTransaction();
//					transaction.account = account;
//					transaction.label = result.getLabel();
//					transaction.amount = result.getAmount();
//					transaction.date = result.getDate();
//					transaction.save();
//				}
//
//				account.lastSync = DateTime.now();
//				account.save();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}
