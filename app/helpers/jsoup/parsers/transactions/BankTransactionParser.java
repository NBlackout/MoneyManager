package helpers.jsoup.parsers.transactions;

import java.util.List;

import org.jsoup.nodes.Document;

public interface BankTransactionParser {

	public List<BankTransactionParserResult> parse(Document document);
}
