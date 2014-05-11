package helpers.jsoup.parsers.accounts;

public class AccountParserResult {

	private String number;

	private String label;

	private double balance;

	private String urlNumber;

	// @formatter:off
	public String getNumber() { return number; }
	public String getLabel() { return label; }
	public double getBalance() { return balance; }
	public String getUrlNumber() { return urlNumber; }

	public void setNumber(String number) { this.number = number; }
	public void setLabel(String label) { this.label = label; }
	public void setBalance(double balance) { this.balance = balance; }
	public void setUrlNumber(String urlNumber) { this.urlNumber = urlNumber; }
	// @formatter:on
}
