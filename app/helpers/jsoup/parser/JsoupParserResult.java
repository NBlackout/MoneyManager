package helpers.jsoup.parser;

public class JsoupParserResult {

	private String number;

	private String label;

	private double balance;

	// @formatter:off
	public String getNumber() { return number; }
	public String getLabel() { return label; }
	public double getBalance() { return balance; }

	public void setNumber(String number) { this.number = number; }
	public void setLabel(String label) { this.label = label; }
	public void setBalance(double balance) { this.balance = balance; }
	// @formatter:on
}
