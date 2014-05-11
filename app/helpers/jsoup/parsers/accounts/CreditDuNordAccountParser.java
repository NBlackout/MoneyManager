package helpers.jsoup.parsers.accounts;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CreditDuNordAccountParser implements BankAccountParser {

	private static final String LABEL_PREFIX = "(" + "<div class=" + "\\\\" + "\"" + "libelleCompteTDB" + "\\\\" + "\"" + ">" + ")";
	private static final String LABEL_GROUP = "(" + "?<label>" + "[^<]*" + ")";
	private static final String LABEL_GROUP_SEPARATOR = "(" + "[<][^<]*" + ")";

	private static final String NUMBER_PREFIX = "(" + "<div class=" + "\\\\" + "\"" + "numCompteTDB" + "\\\\" + "\"" + ">" + ")";
	private static final String NUMBER_GROUP = "(" + "?<number>" + "[^<]*" + ")";
	private static final String NUMBER_GROUP_SEPARATOR = "(" + "[^']*" + ")";

	private static final String VALUE_FRF_PREFIX = "(" + "',[ ]*'" + ")";
	private static final String VALUE_FRF_GROUP = "(" + "?<valueFRF>" + "[0-9\\S]*[,][0-9\\S]*" + ")";
	private static final String VALUE_FRF_VALUE_EUR_SEPARATOR = "(" + "[^']*" + ")";

	private static final String VALUE_EUR_PREFIX = "(" + "',[ ]*'" + ")";
	private static final String VALUE_EUR_GROUP = "(" + "?<valueEUR>" + "[0-9\\S]*[,][0-9\\S]*" + ")";

	@Override
	public List<BankAccountParserResult> parse(Document document) {
		List<BankAccountParserResult> results = new LinkedList<>();

		Element element = document.getElementById("appliSouscription");
		Element script = element.getElementsByTag("script").first();
		String content = script.data();

		String labelPart = LABEL_PREFIX + LABEL_GROUP;
		String numberPart = NUMBER_PREFIX + NUMBER_GROUP;
		String valueFrfPart = VALUE_FRF_PREFIX + VALUE_FRF_GROUP;
		String valueEurPart = VALUE_EUR_PREFIX + VALUE_EUR_GROUP;

		Pattern pattern = Pattern.compile(labelPart + LABEL_GROUP_SEPARATOR + numberPart + NUMBER_GROUP_SEPARATOR + valueFrfPart + VALUE_FRF_VALUE_EUR_SEPARATOR + valueEurPart);
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			BankAccountParserResult result = new BankAccountParserResult();
			result.setNumber(normalizeWhitespaces(matcher.group("number")).trim());
			result.setLabel(normalizeWhitespaces(matcher.group("label")).trim());
			result.setBalance(Double.parseDouble(normalizeWhitespaces(matcher.group("valueEUR")).replace(" ","").trim().replace(",", ".")));

			results.add(result);
		}

		return results;
	}

	private String normalizeWhitespaces(String string) {
		return string.replaceAll("\\p{javaSpaceChar}+", " ");
	}
}
