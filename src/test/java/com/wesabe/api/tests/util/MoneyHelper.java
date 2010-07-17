package com.wesabe.api.tests.util;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;

import java.math.BigDecimal;
import java.util.Currency;

import com.wesabe.api.util.money.Money;

public class MoneyHelper {
	public static final Money oneDollar = new Money(decimal("1.00"), USD);
	public static final Money oneEuro = new Money(decimal("1.00"), EUR);
	public static final Money zeroDollars = new Money(decimal("0.00"), USD);
	public static final Money zeroEuros = new Money(decimal("0.00"), EUR);
	public static final Money zeroPounds = new Money(decimal("0.00"), GBP);
	public static final Money fourDollars = new Money(decimal("4.00"), USD);
	public static final Money twoEuros = new Money(decimal("2.00"), EUR);
	public static final Money oneKroner = new Money(decimal("1.00"), NOK); // eat it, DHH
	public static final Money sixKroners = new Money(decimal("6.00"), NOK);
	
	public static Money money(String amount, Currency currency) {
		return new Money(new BigDecimal(amount), currency);
	}
}