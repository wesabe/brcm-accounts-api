package com.wesabe.api.util.money;

import java.util.Currency;
import java.util.Map;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * A class responsible for converting potentially outdated or incorrect
 * ISO 4217 currency codes into {@link Currency} instances. <b>Use
 * this class instead of {@link Currency#getInstance(String)}.</b>
 * 
 * <p>
 * {@link Currency#getInstance(String)} will throw a rather uninformative
 * {@link IllegalArgumentException} if the currency code is not recognized.
 * {@link CurrencyCodeParser#parse(String)} will map outdated currencies to their
 * modern equivalents, and throws an {@link UnknownCurrencyCodeException} with
 * the offending currency code front and center.
 * </p>
 * 
 * @author coda
 * 
 */
@Immutable
public class CurrencyCodeParser {
	private static final String[] OUTDATED_CURRENCIES = new String [] {
		"",    "USD", // Blank, mystery currencies
		"ADF", "EUR", // Andorran franc
		"MCF", "EUR", // Monegasque franc
		"VAL", "EUR", // Vatican lira
		"XEU", "EUR", // European Currency Unit
		"ALK", "AFN", // Albanian old lek
		"AON", "AOA", // Angolan new kwanza
		"AOR", "AOA", // Angolan kwanza readjustado
		"ARM", "ARS", // Argentine peso moneda nacional
		"ARL", "ARS", // Argentine peso ley
		"ARP", "ARS", // Peso argentino
		"ARA", "ARS", // Argentine austral
		"BGJ", "BGN", // Bulgarian lev A/52
		"BGK", "BGN", // Bulgarian lev A/62
		"BOP", "BOB", // Bolivian peso
		"BRB", "BRL", // Brazilian cruzeiro novo
		"BRC", "BRL", // Brazilian cruzado
		"BRE", "BRL", // Brazilian cruzeiro
		"BRN", "BRL", // Brazilian cruzado novo
		"BRR", "BRL", // Brazilian cruzeiro real
		"BRZ", "BRL", // Brazilian cruzeiro
		"CLE", "CLP", // Chilean escudo
		"CNX", "CNY", // Chinese People's Bank dollar
		"CSJ", "CZK", // Czechoslovak koruna A/53
		"CSK", "CZK", // Czechoslovak koruna
		"DDM", "DEM", // East German Mark of the GDR (East Germany)
		"ECS", "USD", // Ecuador sucre
		"EQE", "XAF", // Equatorial Guinean ekwele
		"ESA", "ESP", // Spanish peseta (account A)
		"ESB", "ESP", // Spanish peseta (account B)
		"GNE", "XOF", // Guinean syli
		"ILP", "ILS", // Israeli lira
		"ILR", "ILS", // Israeli old sheqel
		"ISJ", "ISK", // Icelandic old krona
		"LAJ", "LAK", // Lao kip
		"MKN", "MKD", // Former Yugoslav Republic of Macedonia dinar A/93
		"MLF", "XOF", // Mali franc
		"MVQ", "MVR", // Maldive rupee
		"MXP", "MXN", // Mexican peso
		"NFD", "CAD", // Newfoundland dollar
		"PEH", "PEN", // Peruvian sol
		"PEI", "PEN", // Peruvian inti
		"PLZ", "PLN", // Polish zloty A/94
		"SUR", "RUR", // Soviet Union rouble
		"TJR", "TJS", // Tajikistan rouble
		"UAK", "UAH", // Ukrainian karbovanets
		"UGS", "UGX", // Ugandan shilling A/87
		"UYN", "UYU", // Uruguay old peso
		"VEB", "VEF", // Venezuelan bolívar
		"YDD", "YER", // South Yemeni dinar
		"YUD", "CSD", // New Yugoslav dinar
		"ZRN", "CDF", // Zaïrean new zaïre
		"ZRZ", "CDF", // Zaïrean zaïre
		"ZWC", "ZWD", // Zimbabwe Rhodesian dollar
		"YTL", "TRY", // Turkish new lira (erroneous)
	};
	
	private final Map<String, Currency> currencies;
	
	/**
	 * A map of outdated currencies which {@link Currency} doesn't recognize
	 * and their modern equivalents. Poached from Wikipedia's article on ISO
	 * 4217.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/ISO_4217#Obsolete_currency_codes">ISO 4217</a>
	 */
	public CurrencyCodeParser() {
		Builder<String, Currency> builder = ImmutableMap.builder();
		for (int i = 0; i < OUTDATED_CURRENCIES.length; i += 2) {
			builder.put(OUTDATED_CURRENCIES[i], Currency.getInstance(OUTDATED_CURRENCIES[i + 1]));
		}
		this.currencies = builder.build();
	}

	/**
	 * Converts an ISO 4217 currency code to a {@link Currency} object.
	 * 
	 * @param currencyCode a ISO 4217 currency code
	 * @return the {@link Currency} which matches {@code currencyCode}
	 * @throws UnknownCurrencyCodeException if {@code currencyCode} is unknown
	 */
	public Currency parse(String currencyCode)
			throws UnknownCurrencyCodeException {
		try {
			final Currency foundCurrency = currencies.get(currencyCode);
			if (foundCurrency == null) {
				return Currency.getInstance(currencyCode);
			}
			return foundCurrency;
		} catch (IllegalArgumentException e) {
			throw new UnknownCurrencyCodeException(currencyCode, e);
		}
	}
}
