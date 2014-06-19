package extensions;

import java.text.NumberFormat;
import java.util.Currency;

import play.i18n.Lang;
import play.libs.I18N;
import play.templates.JavaExtensions;

public class CustomExtensions extends JavaExtensions {

	public static String formatCurrencyEur(Number number) {
		Currency currency = Currency.getInstance("EUR");

		NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Lang.getLocale());
		numberFormat.setCurrency(currency);
		numberFormat.setMaximumFractionDigits(currency.getDefaultFractionDigits());

		String s = numberFormat.format(number);
		s = s.replace("EUR", I18N.getCurrencySymbol("EUR"));

		return s;
	}
}
