package helpers.jsoup.parsers.accounts;

import java.util.List;

import org.jsoup.nodes.Document;

public interface IAccountParser {

	public List<AccountParserResult> parse(Document document);
}
