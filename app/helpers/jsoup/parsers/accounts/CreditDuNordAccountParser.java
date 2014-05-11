package helpers.jsoup.parsers.accounts;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CreditDuNordAccountParser implements AccountParser {

	private static final String SEP = "('[,]')";

	private static final String ID_GROUP = "id";
	private static final String URL_NUMBER_GROUP = "urlNumber";
	private static final String LABEL_NUMBER_GROUP = "label";
	private static final String VALUE_FRF_GROUP = "valueFRF";
	private static final String VALUE_EUR_GROUP = "valueEUR";

	// private static final String LABEL_PREFIX = "(" + "<div class=" + "\\\\" + "\"" + "libelleCompteTDB" + "\\\\" + "\"" + ">" + ")";
	// private static final String LABEL_GROUP = "(" + "?<label>" + "[^<]*" + ")";
	// private static final String LABEL_GROUP_SEPARATOR = "(" + "[<][^<]*" + ")";
	//
	// private static final String NUMBER_PREFIX = "(" + "<div class=" + "\\\\" + "\"" + "numCompteTDB" + "\\\\" + "\"" + ">" + ")";
	// private static final String NUMBER_GROUP = "(" + "?<number>" + "[^<]*" + ")";
	// private static final String NUMBER_GROUP_SEPARATOR = "(" + "[^']*" + ")";
	//
	// private static final String VALUE_FRF_PREFIX = "(" + "',[ ]*'" + ")";
	// private static final String VALUE_FRF_GROUP = "(" + "?<valueFRF>" + "[0-9\\S]*[,][0-9\\S]*" + ")";
	// private static final String VALUE_FRF_VALUE_EUR_SEPARATOR = "(" + "[^']*" + ")";
	//
	// private static final String VALUE_EUR_PREFIX = "(" + "',[ ]*'" + ")";
	// private static final String VALUE_EUR_GROUP = "(" + "?<valueEUR>" + "[0-9\\S]*[,][0-9\\S]*" + ")";

	@Override
	public List<AccountParserResult> parse(Document document) {
		List<AccountParserResult> results = new LinkedList<>();

		Element element = document.getElementById("appliSouscription");
		Element script = element.getElementsByTag("script").first();
		String content = script.data();

		String id = "(?<" + ID_GROUP + ">" + "[^']*)";
		String anything = "([^']*)";
		String urlNumberPart = "(?<" + URL_NUMBER_GROUP + ">" + "[^']*)";
		String label = "(?<" + LABEL_NUMBER_GROUP + ">" + "[^']*)";
		String valueFrf = "(?<" + VALUE_FRF_GROUP + ">" + "[^']*)";
		String valueEur = "(?<" + VALUE_EUR_GROUP + ">" + "[^']*)";

		Pattern pattern = Pattern.compile("[\\[]'" + id + SEP + anything + SEP + anything + SEP + anything + SEP + label + SEP + valueFrf + SEP + valueEur + "'[\\]]");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			AccountParserResult result = new AccountParserResult();
			result.setNumber(extract(matcher.group(LABEL_NUMBER_GROUP)));
			result.setLabel(extract(matcher.group(LABEL_NUMBER_GROUP)));
			result.setBalance(Double.parseDouble(extract(matcher.group(VALUE_EUR_GROUP)).replace(" ", "").replace(",", ".")));
			result.setNumber(extract(matcher.group(URL_NUMBER_GROUP)));
			results.add(result);
		}

		return results;
	}

	private String extract(String string) {
		return string.replace("<br/>&nbsp;", "").replaceAll("\\p{javaSpaceChar}+", " ").trim();
	}
}
