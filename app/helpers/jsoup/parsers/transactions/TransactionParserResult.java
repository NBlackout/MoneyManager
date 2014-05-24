package helpers.jsoup.parsers.transactions;

import org.joda.time.DateTime;

public class TransactionParserResult {

	private String label;

	private String additionalLabel;

	private double amount;

	private DateTime valueDate;

	private DateTime recordingDate;

	// @formatter:off
	public String getLabel() { return label; }
	public String getAdditionalLabel() { return additionalLabel; }
	public double getAmount() { return amount; }
	public DateTime getValueDate() { return valueDate; }
	public DateTime getRecordingDate() { return recordingDate; }

	public void setLabel(String label) { this.label = label; }
	public void setAdditionalLabel(String additionalLabel) { this.additionalLabel = additionalLabel; }
	public void setAmount(double amount) { this.amount = amount; }
	public void setValueDate(DateTime valueDate) { this.valueDate = valueDate; }
	public void setRecordingDate(DateTime recordingDate) { this.recordingDate = recordingDate; }
	// @formatter:on
}
