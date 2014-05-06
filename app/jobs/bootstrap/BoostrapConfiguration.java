package jobs.bootstrap;

import java.util.Arrays;
import java.util.List;

public class BoostrapConfiguration {

	public static final int ACCOUNT_COUNT = 5;

	public static final int ACCOUNT_MAX_BALANCE = 1000000;

	public static final List<String> PERIODICITY_LABELS = Arrays.asList("Annuelle", "Semestrielle", "Trimestrielle", "Mensuelle");

	public static final List<String> CATEGORY_LABELS = Arrays.asList("Assurance", "Loyer", "Électricité", "Internet/Téléphone");

	public static final int MAX_YEARS_HISTORY = 0;

	public static final int MAX_PER_DAY = 3;

	public static final int MIN_AMOUNT = -200;

	public static final int MAX_AMOUNT = 100;
}
