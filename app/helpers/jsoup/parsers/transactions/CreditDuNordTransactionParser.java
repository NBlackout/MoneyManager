package helpers.jsoup.parsers.transactions;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CreditDuNordTransactionParser implements BankTransactionParser {

	private static final String SEP = "('[,]')";

	private static final String ID_GROUP = "id";
	private static final String DATE_GROUP = "date";
	private static final String DATE_VALUE_GROUP = "dateValue";
	private static final String LABEL_GROUP = "label";
	private static final String VALUE_FRF_GROUP = "valueFRF";
	private static final String VALUE_EUR_GROUP = "valueEUR";

	@Override
	public List<BankTransactionParserResult> parse(Document document) {
		List<BankTransactionParserResult> results = new LinkedList<>();

		Element element = document.getElementById("appliSouscription");
		Element script = element.getElementsByTag("script").first();
		String content = script.data();

		String id = "(?<" + ID_GROUP + ">" + "[^']*)";
		String date = "(?<" + DATE_GROUP + ">" + "[^']*)";
		String dateValue = "(?<" + DATE_VALUE_GROUP + ">" + "[^']*)";
		String label = "(?<" + LABEL_GROUP + ">" + "[^']*)";
		String valueFrf = "(?<" + VALUE_FRF_GROUP + ">" + "[^']*)";
		String valueEur = "(?<" + VALUE_EUR_GROUP + ">" + "[^']*)";

		Pattern pattern = Pattern.compile("[\\[]'" + id + SEP + date + SEP + dateValue + SEP + label + SEP + valueFrf + SEP + valueEur + "'[\\]]");
		Matcher matcher = pattern.matcher(content);

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		while (matcher.find()) {
			BankTransactionParserResult result = new BankTransactionParserResult();
			result.setLabel(extract(matcher.group(LABEL_GROUP)));
			result.setAmount(Double.parseDouble(extract(matcher.group(VALUE_EUR_GROUP)).replace(" ", "").replace(",", ".")));
			result.setDate(DateTime.parse(extract(matcher.group(DATE_GROUP)), formatter));

			results.add(result);
		}

		return results;
	}

	private String extract(String string) {
		return string.replace("<br/>&nbsp;", "").replaceAll("\\p{javaSpaceChar}+", " ").trim();
	}
}
