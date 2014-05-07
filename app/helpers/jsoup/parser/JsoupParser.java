package helpers.jsoup.parser;

import java.util.List;

import org.jsoup.nodes.Document;

public interface JsoupParser {
	
	public List<JsoupParserResult> parse(Document document);
}
