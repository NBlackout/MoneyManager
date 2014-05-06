package helpers.jsoup;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupParser {

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

	public static List<Map<String, String>> parse(Document document) throws Exception {
		List<Map<String, String>> rows = new LinkedList<>();

		Element element = document.getElementById("appliSouscription");
		Element script = element.getElementsByTag("script").first();
		String content = script.data();

		String labelPart = LABEL_PREFIX + LABEL_GROUP;
		String numberPart = NUMBER_PREFIX + NUMBER_GROUP;
		String valueFrfPart = VALUE_FRF_PREFIX + VALUE_FRF_GROUP;
		String valueEurPart = VALUE_EUR_PREFIX + VALUE_EUR_GROUP;

		Pattern pattern = Pattern.compile(labelPart + LABEL_GROUP_SEPARATOR + numberPart + NUMBER_GROUP_SEPARATOR + valueFrfPart + VALUE_FRF_VALUE_EUR_SEPARATOR + valueEurPart);
		Matcher matcher = pattern.matcher(content);

		Set<String> groupNames = getPatternNamedGroups(pattern);
		while (matcher.find()) {
			Map<String, String> groups = new HashMap<>();

			for (String groupName : groupNames) {
				String groupValue = matcher.group(groupName);
				groups.put(groupName, groupValue);
			}

			rows.add(groups);
		}

		return rows;
	}

	private static Set<String> getPatternNamedGroups(Pattern pattern) throws Exception {
		Method namedGroupsMethod = Pattern.class.getDeclaredMethod("namedGroups");
		namedGroupsMethod.setAccessible(true);

		Map<String, Integer> namedGroups = (Map<String, Integer>) namedGroupsMethod.invoke(pattern);
		Set<String> groupNames = namedGroups.keySet();

		return groupNames;
	}
}
