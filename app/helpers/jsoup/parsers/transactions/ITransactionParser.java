package helpers.jsoup.parsers.transactions;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ITransactionParser {

	public List<TransactionParserResult> parse(ByteArrayInputStream input);
}
