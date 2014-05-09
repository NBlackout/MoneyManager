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

public class CreditDuNordTransactionParser implements TransactionParser {

	private static final String GROUP_SEPARATOR = "(" + "[^']*'[,]" + ")";

	private static final String ID_PREFIX = "(" + "[^\\[]*[\\[]'" + ")";
	private static final String ID_GROUP = "(" + "?<id>" + "[^']*" + ")";

	private static final String DATE_PREFIX = "(" + "'" + ")";
	private static final String DATE_GROUP = "(" + "?<date>" + "[^<]*" + ")";

	private static final String DATE_VALUE_PREFIX = "(" + "'" + ")";
	private static final String DATE_VALUE_GROUP = "(" + "?<dateValue>" + "[^<]*" + ")";

	private static final String LABEL_PREFIX = "(" + "'" + ")";
	private static final String LABEL_GROUP = "(" + "?<label>" + "[^<]*" + ")";

	private static final String VALUE_FRF_PREFIX = "(" + "'" + ")";
	private static final String VALUE_FRF_GROUP = "(" + "?<valueFRF>" + "[^<]*" + ")";

	private static final String VALUE_EUR_PREFIX = "(" + "'" + ")";
	private static final String VALUE_EUR_GROUP = "(" + "?<valueEUR>" + "[^<]*" + ")";

	@Override
	public List<TransactionParserResult> parse(Document document) {
		List<TransactionParserResult> results = new LinkedList<>();

		Element element = document.getElementById("appliSouscription");
		Element script = element.getElementsByTag("script").first();
		String content = script.data();

		String idPart = ID_PREFIX + ID_GROUP;
		String datePart = DATE_PREFIX + DATE_GROUP;
		String dateValuePart = DATE_VALUE_PREFIX + DATE_VALUE_GROUP;
		String labelPart = LABEL_PREFIX + LABEL_GROUP;
		String valueFrfPart = VALUE_FRF_PREFIX + VALUE_FRF_GROUP;
		String valueEurPart = VALUE_EUR_PREFIX + VALUE_EUR_GROUP;

		Pattern pattern = Pattern.compile(idPart + GROUP_SEPARATOR + datePart + GROUP_SEPARATOR + dateValuePart + GROUP_SEPARATOR + labelPart + GROUP_SEPARATOR + valueFrfPart + GROUP_SEPARATOR + valueEurPart);
		Matcher matcher = pattern.matcher(content);

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		while (matcher.find()) {
			TransactionParserResult result = new TransactionParserResult();
			result.setLabel(normalizeWhitespaces(matcher.group("label")).trim());
			result.setAmount(Double.parseDouble(normalizeWhitespaces(matcher.group("valueEUR")).replace(" ","").trim().replace(",", ".")));
			result.setDate(DateTime.parse(normalizeWhitespaces(matcher.group("date")).trim(), formatter));
//
			System.out.println(result.getLabel());
			System.out.println(result.getAmount());
			System.out.println(result.getDate());
//			results.add(result);
		}

		return results;
	}

	private String normalizeWhitespaces(String string) {
		return string.replaceAll("\\p{javaSpaceChar}+", " ");
	}
}
