package helpers.jsoup.parsers.transactions;

import org.joda.time.DateTime;

public class BankTransactionParserResult {

	private String label;

	private double amount;

	private DateTime date;

	// @formatter:off
	public String getLabel() { return label; }
	public double getAmount() { return amount; }
	public DateTime getDate() { return date; }

	public void setLabel(String label) { this.label = label; }
	public void setAmount(double amount) { this.amount = amount; }
	public void setDate(DateTime date) { this.date = date; }
	// @formatter:on
}
