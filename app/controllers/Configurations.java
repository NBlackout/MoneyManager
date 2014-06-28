package controllers;

import java.util.List;

import jobs.generators.RegularTransactionGenerator;
import models.Account;
import models.transactions.regular.Category;
import models.transactions.regular.Configuration;
import models.transactions.regular.Periodicity;
import models.transactions.regular.RegularTransaction;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import play.data.binding.As;

public class Configurations extends SuperController {

	public static void save(Long configurationId, Long accountId, Long categoryId, String fixedLabel, String friendlyLabel, @As("dd/MM/yyyy") DateTime dueDate) {
		Account account = Account.findById(accountId);

		if (categoryId != null && StringUtils.isNotBlank(fixedLabel) && StringUtils.isNotBlank(friendlyLabel) && dueDate != null) {
			if (configurationId != null) {
				Configuration configuration = Configuration.findById(configurationId);
				configuration.category = Category.findById(categoryId);
				configuration.fixedLabel = fixedLabel;
				configuration.friendlyLabel = friendlyLabel;
				configuration.save();
			} else {
				Configuration configuration = new Configuration();
				configuration.account = account;
				configuration.category = Category.findById(categoryId);
				configuration.periodicity = Periodicity.find("byLabel", "Mensuelle").first();
				configuration.fixedLabel = fixedLabel;
				configuration.friendlyLabel = friendlyLabel;
				configuration.firstDueDate = dueDate;
				configuration.lastDueDate = null;
				configuration.active = true;
				configuration.save();

				new RegularTransactionGenerator(configuration.id).now();
			}

			Accounts.show(account.id, dueDate.getYear(), dueDate.getMonthOfYear());
		} else {
			Accounts.show(account.id, null, null);
		}
	}

	public static void toggle(Long configurationId, Integer year, Integer month) {
		Configuration configuration = Configuration.findById(configurationId);
		configuration.active = (configuration.active == false);
		configuration.save();

		Accounts.show(configuration.account.id, year, month);
	}

	public static void delete(Long configurationId, Integer year, Integer month) {
		Configuration configuration = Configuration.findById(configurationId);

		List<RegularTransaction> transactions = RegularTransaction.find("byConfiguration", configuration).fetch();
		for (RegularTransaction transaction : transactions) {
			transaction.delete();
		}

		configuration.delete();

		Accounts.show(configuration.account.id, year, month);
	}

	public static void generate(Long configurationId) {
		new RegularTransactionGenerator(configurationId).now();

		Configuration configuration = Configuration.findById(configurationId);
		DateTime date = configuration.lastDueDate;

		Accounts.show(configuration.account.id, date.getYear(), date.getMonthOfYear());
	}
}
