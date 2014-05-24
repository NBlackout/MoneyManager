package helpers.jsoup.parsers.accounts;

public class AccountParserResult {

	private String agency;

	private String rank;

	private String series;

	private String subAccount;

	private String label;

	private double balance;

	// @formatter:off
	public String getAgency() { return agency; }
	public String getRank() { return rank; }
	public String getSeries() { return series; }
	public String getSubAccount() { return subAccount; }
	public String getLabel() { return label; }
	public double getBalance() { return balance; }

	public void setAgency(String agency) { this.agency = agency; }
	public void setRank(String rank) { this.rank = rank; }
	public void setSeries(String series) { this.series = series; }
	public void setSubAccount(String subAccount) { this.subAccount = subAccount; }
	public void setLabel(String label) { this.label = label; }
	public void setBalance(double balance) { this.balance = balance; }
	// @formatter:on
}
