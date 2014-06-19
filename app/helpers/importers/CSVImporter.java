package helpers.importers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import models.Bank;
import models.BankType;

public class CSVImporter implements Importer {

	private File file;

	public CSVImporter(File file) {
		this.file = file;
	}

	@Override
	public void importData() {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(";");
				String number = split[0];
				String label = split[1];
				BankType type = BankType.valueOf(split[2]);

				Bank bank = new Bank();
				bank.number = number;
				bank.label = label;
				bank.type = type;
				bank.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
