package helpers.jsoup.parsers.accounts;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CreditDuNordAccountParser implements IAccountParser {

	private static final String SEP = "('[,][ ]?')";

	private static final String ID_GROUP = "id";

	private static final String URL_NUMBER_GROUP = "urlNumber";

	private static final String LABEL_AND_NUMBERS_GROUP = "labelNumber";

	private static final String VALUE_FRF_GROUP = "valueFRF";

	private static final String VALUE_EUR_GROUP = "valueEUR";

	@Override
	public List<AccountParserResult> parse(Document document) {
		List<AccountParserResult> results = new LinkedList<>();

		Element element = document.getElementById("appliSouscription");
		Element script = element.getElementsByTag("script").first();
		String content = script.data();

		String id = "(?<" + ID_GROUP + ">" + "[^']+)";
		String wildcard = "([^']*)";
		String urlNumber = "(?<" + URL_NUMBER_GROUP + ">" + "[^']+)";
		String labelAndNumber = "(?<" + LABEL_AND_NUMBERS_GROUP + ">" + "[^']+)";
		String valueFrf = "(?<" + VALUE_FRF_GROUP + ">" + "[^']+)";
		String valueEur = "(?<" + VALUE_EUR_GROUP + ">" + "[^']+)";

		Pattern pattern = Pattern.compile("[\\[]'" + id + SEP + wildcard + SEP + wildcard + SEP + wildcard + SEP + urlNumber + SEP + labelAndNumber + SEP + valueFrf + SEP + valueEur + "'[ ]?[\\]]");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			Document labelAndNumbers = Jsoup.parse(matcher.group(LABEL_AND_NUMBERS_GROUP).replace("\\\"", "\""));
			String numbers[] = normalize(labelAndNumbers.select("div.numCompteTDB").first().text()).split(" ");

			String agency = numbers[0];
			String rank = numbers[1];
			String series = numbers[2];
			String subAccount = numbers[3];
			String label = normalize(labelAndNumbers.select("div.libelleCompteTDB").first().text());
			Double balance = Double.parseDouble(normalize(matcher.group(VALUE_EUR_GROUP)).replaceAll(" |EUR", "").replace(",", "."));

			AccountParserResult result = new AccountParserResult();
			result.setAgency(agency);
			result.setRank(rank);
			result.setSeries(series);
			result.setSubAccount(subAccount);
			result.setLabel(label);
			result.setBalance(balance);

			results.add(result);
		}

		return results;
	}

	private String normalize(String string) {
		return Jsoup.parse(string).text().replaceAll("\\p{javaSpaceChar}", " ").trim();
	}
}
