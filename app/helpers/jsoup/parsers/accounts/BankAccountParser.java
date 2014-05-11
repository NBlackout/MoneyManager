package helpers.jsoup.parsers.accounts;

import java.util.List;

import org.jsoup.nodes.Document;

public interface BankAccountParser {

	public List<BankAccountParserResult> parse(Document document);
}
