package helpers.jsoup.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CreditDuNordParser implements JsoupParser {

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
	public List<JsoupParserResult> parse(Document document) {
		List<JsoupParserResult> results = new LinkedList<>();

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
			JsoupParserResult result = new JsoupParserResult();
			result.setNumber(normalizeWhitespaces(matcher.group("number")));
			result.setLabel(normalizeWhitespaces(matcher.group("label")));
			result.setBalance(Double.parseDouble(normalizeWhitespaces(matcher.group("valueEUR")).replace(" ","").replace(",", ".")));

			results.add(result);
		}

		return results;
	}

	private String normalizeWhitespaces(String string) {
		return string.replaceAll("\\p{javaSpaceChar}+", " ");
	}
}
