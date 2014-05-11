package models;

import helpers.jsoup.JsoupConnection;
import helpers.jsoup.parsers.accounts.BankAccountParserResult;
import helpers.jsoup.parsers.accounts.CreditDuNordAccountParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public enum BankWebSite {

	CreditDuNord;
}
