package controllers;

import java.util.List;

import jobs.crawlers.BankCrawler;
import models.Bank;
import play.mvc.Controller;

public class Banks extends Controller {

	public static void index() {
		List<Bank> banks = Bank.findAll();

		render(banks);
	}

	public static void synchronize(long bankId, String login, String password) {
		new BankCrawler(bankId, login, password).now();

		index();
	}
}
