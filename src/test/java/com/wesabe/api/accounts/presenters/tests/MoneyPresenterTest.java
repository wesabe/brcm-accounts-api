package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.util.money.Money;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class MoneyPresenterTest {
	public static class Presenting_A_Monetary_Amount {
		private final MoneyPresenter presenter = new MoneyPresenter();
		private final Money amount = money("-44.56", USD);
		
		@Test
		public void itUsesTheSpecifiedName() throws Exception {
			final XmlsonObject representation = presenter.present("amount", amount, Locale.GERMANY);
			assertThat(representation.getName(), is("amount"));
		}
		
		@Test
		public void itIncludesTheAmountAsAFloatingPointNumberInStringForm() throws Exception {
			final XmlsonObject representation = presenter.present("amount", amount, Locale.GERMANY);
			
			assertThat(representation.getString("value"), is("-44.56"));
		}
		
		@Test
		public void itIncludesTheAmountAsAMonetaryAmountFormattedInTheGivenLocale() throws Exception {
			final XmlsonObject representation = presenter.present("amount", amount, Locale.GERMANY);
			
			assertThat(representation.getString("display"), is("-44,56 $"));
		}
	}
}
