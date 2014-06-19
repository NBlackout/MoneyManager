package controllers;

import helpers.importers.CSVImporter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.ManyToOne;

import models.Account;
import models.Bank;
import models.Customer;
import models.transactions.oneoff.OneOffTransaction;
import models.transactions.regular.Category;
import models.transactions.regular.Configuration;
import models.transactions.regular.Periodicity;
import models.transactions.regular.RegularTransaction;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.i18n.Lang;
import play.i18n.Messages;

public class DataManagement extends SuperController {

	public static void index() {
		render();
	}

	public static void exportData() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(baos))) {
			// Banks export
			zos.putNextEntry(new ZipEntry(Messages.get("labels.banks") + ".csv"));

			StringBuilder builder = new StringBuilder();
			{
				builder.append(Messages.get("labels.number"));
				builder.append(";");
				builder.append(Messages.get("labels.label"));
				builder.append(";");
				builder.append(Messages.get("labels.bank.type"));
				builder.append(";");
				builder.append(Messages.get("labels.lastSync"));
				builder.append("\n");

				List<Bank> banks = Bank.find("ORDER BY number ASC").fetch();
				for (Bank bank : banks) {
					builder.append(bank.number);
					builder.append(";");
					builder.append(bank.label);
					builder.append(";");
					builder.append(bank.type);
					builder.append(";");
					builder.append(bank.getLocalLastSync().getMillis());
					builder.append("\n");
				}
			}

			zos.write(0xEF);
			zos.write(0xBB);
			zos.write(0xBF);
			zos.write(builder.toString().getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();

			// Customers export
			zos.putNextEntry(new ZipEntry(Messages.get("labels.customers") + ".csv"));

			builder = new StringBuilder();
			{
				builder.append(Messages.get("labels.bank"));
				builder.append(";");
				builder.append(Messages.get("labels.login"));
				builder.append(";");
				builder.append(Messages.get("labels.password"));
				builder.append(";");
				builder.append(Messages.get("labels.firstName"));
				builder.append(";");
				builder.append(Messages.get("labels.lastName"));
				builder.append("\n");

				List<Customer> customers = Customer.find("ORDER BY bank.id ASC, login ASC").fetch();
				for (Customer customer : customers) {
					builder.append(customer.bank.id);
					builder.append(";");
					builder.append(customer.login);
					builder.append(";");
					builder.append(customer.password);
					builder.append(";");
					builder.append(customer.firstName);
					builder.append(";");
					builder.append(customer.lastName);
					builder.append("\n");
				}
			}

			zos.write(0xEF);
			zos.write(0xBB);
			zos.write(0xBF);
			zos.write(builder.toString().getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();

			// Accounts export
			zos.putNextEntry(new ZipEntry(Messages.get("labels.accounts") + ".csv"));

			builder = new StringBuilder();
			{
				builder.append(Messages.get("labels.customer"));
				builder.append(";");
				builder.append(Messages.get("labels.agency"));
				builder.append(";");
				builder.append(Messages.get("labels.rank"));
				builder.append(";");
				builder.append(Messages.get("labels.series"));
				builder.append(";");
				builder.append(Messages.get("labels.subAccount"));
				builder.append(";");
				builder.append(Messages.get("labels.label"));
				builder.append(";");
				builder.append(Messages.get("labels.balance"));
				builder.append(";");
				builder.append(Messages.get("labels.lastSync"));
				builder.append("\n");

				List<Account> accounts = Account.find("ORDER BY customer.id ASC, agency ASC, rank ASC, series ASC, subAccount ASC").fetch();
				for (Account account : accounts) {
					builder.append(account.customer.id);
					builder.append(";");
					builder.append(account.agency);
					builder.append(";");
					builder.append(account.rank);
					builder.append(";");
					builder.append(account.series);
					builder.append(";");
					builder.append(account.subAccount);
					builder.append(";");
					builder.append(account.label);
					builder.append(";");
					builder.append(account.balance);
					builder.append(";");
					builder.append(account.lastSync.getMillis());
					builder.append("\n");
				}
			}

			zos.write(0xEF);
			zos.write(0xBB);
			zos.write(0xBF);
			zos.write(builder.toString().getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();

			// One-off transactions export
			zos.putNextEntry(new ZipEntry(Messages.get("labels.transactions.one-off") + ".csv"));

			builder = new StringBuilder();
			{
				builder.append(Messages.get("labels.account"));
				builder.append(";");
				builder.append(Messages.get("labels.label"));
				builder.append(";");
				builder.append(Messages.get("labels.label.additional"));
				builder.append(";");
				builder.append(Messages.get("labels.amount"));
				builder.append(";");
				builder.append(Messages.get("labels.date"));
				builder.append("\n");

				List<OneOffTransaction> transactions = OneOffTransaction.find("ORDER BY account.id ASC, date DESC, label ASC").fetch();
				for (OneOffTransaction transaction : transactions) {
					builder.append(transaction.account.id);
					builder.append(";");
					builder.append(transaction.label);
					builder.append(";");
					builder.append(ObjectUtils.defaultIfNull(transaction.additionalLabel, ""));
					builder.append(";");
					builder.append(transaction.amount);
					builder.append(";");
					builder.append(transaction.date.getMillis());
					builder.append("\n");
				}
			}

			zos.write(0xEF);
			zos.write(0xBB);
			zos.write(0xBF);
			zos.write(builder.toString().getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();

			// Configurations export
			zos.putNextEntry(new ZipEntry(Messages.get("labels.configuration") + ".csv"));

			builder = new StringBuilder();
			{
				builder.append(Messages.get("labels.account"));
				builder.append(";");
				builder.append(Messages.get("labels.category"));
				builder.append(";");
				builder.append(Messages.get("labels.periodicity"));
				builder.append(";");
				builder.append(Messages.get("labels.label"));
				builder.append(";");
				builder.append(Messages.get("labels.label.friendly"));
				builder.append(";");
				builder.append(Messages.get("labels.date.firstDue"));
				builder.append(";");
				builder.append(Messages.get("labels.date.lastDue"));
				builder.append(";");
				builder.append(Messages.get("labels.active"));
				builder.append("\n");

				List<Configuration> configurations = Configuration.find("ORDER BY account.id ASC, category.id ASC, periodicity.id ASC, label ASC").fetch();
				for (Configuration configuration : configurations) {
					builder.append(configuration.account.id);
					builder.append(";");
					builder.append(configuration.category.id);
					builder.append(";");
					builder.append(configuration.periodicity.id);
					builder.append(";");
					builder.append(configuration.label);
					builder.append(";");
					builder.append(configuration.friendlyLabel);
					builder.append(";");
					builder.append(configuration.firstDueDate.getMillis());
					builder.append(";");
					builder.append(configuration.lastDueDate.getMillis());
					builder.append(";");
					builder.append(configuration.active);
					builder.append("\n");
				}
			}

			zos.write(0xEF);
			zos.write(0xBB);
			zos.write(0xBF);
			zos.write(builder.toString().getBytes(Charset.forName("UTF-8")));

			// Regular transactions export
			zos.putNextEntry(new ZipEntry(Messages.get("labels.transactions.regular") + ".csv"));

			builder = new StringBuilder();
			{
				builder.append(Messages.get("labels.configuration"));
				builder.append(";");
				builder.append(Messages.get("labels.label.additional"));
				builder.append(";");
				builder.append(Messages.get("labels.amount"));
				builder.append(";");
				builder.append(Messages.get("labels.date"));
				builder.append(";");
				builder.append(Messages.get("labels.done"));
				builder.append("\n");

				List<RegularTransaction> transactions = RegularTransaction.find("ORDER BY configuration.id ASC, date DESC, additionalLabel ASC").fetch();
				for (RegularTransaction transaction : transactions) {
					builder.append(transaction.configuration.id);
					builder.append(";");
					builder.append(transaction.additionalLabel);
					builder.append(";");
					builder.append(transaction.amount);
					builder.append(";");
					builder.append(transaction.date);
					builder.append(";");
					builder.append(transaction.done);
					builder.append("\n");
				}
			}

			zos.write(0xEF);
			zos.write(0xBB);
			zos.write(0xBF);
			zos.write(builder.toString().getBytes(Charset.forName("UTF-8")));
			zos.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input = new ByteArrayInputStream(baos.toByteArray());

		renderBinary(input, "export.zip");
	}

	public static void importData(File file) {
		CSVImporter importer = new CSVImporter(file);
		importer.importData();

		index();
	}
}
