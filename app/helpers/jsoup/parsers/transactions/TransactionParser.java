package helpers.jsoup.parsers.transactions;

import java.util.List;

import org.jsoup.nodes.Document;

public interface TransactionParser {

	public List<TransactionParserResult> parse(Document document);
}
