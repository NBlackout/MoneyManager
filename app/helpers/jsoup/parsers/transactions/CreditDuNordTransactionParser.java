package helpers.jsoup.parsers.transactions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CreditDuNordTransactionParser implements ITransactionParser {

	@Override
	public List<TransactionParserResult> parse(ByteArrayInputStream input) {
		List<TransactionParserResult> results = new LinkedList<>();

		try (InputStreamReader isr = new InputStreamReader(input); BufferedReader reader = new BufferedReader(isr)) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("\t", -1);
				if (split != null && split.length == 10) {
					Double amount = Double.parseDouble((split[4].trim().isEmpty() == false) ? split[4].trim().replace(",", ".") : split[5].trim().replace(",", "."));
					String label = split[8].replace("[\\s]+", " ").trim();
					String additionalLabel = (split[9].replace("[\\s]+", " ").trim().isEmpty() == false) ? split[9].replace("[\\s]+", " ").trim() : null;
					DateTime valueDate = DateTime.parse(split[7].trim(), formatter);
					DateTime recordingDate = DateTime.parse(split[6].trim(), formatter);

					TransactionParserResult result = new TransactionParserResult();
					result.setLabel(label);
					result.setAdditionalLabel(additionalLabel);
					result.setAmount(amount);
					result.setValueDate(valueDate);
					result.setRecordingDate(recordingDate);

					results.add(result);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return results;
	}
}
