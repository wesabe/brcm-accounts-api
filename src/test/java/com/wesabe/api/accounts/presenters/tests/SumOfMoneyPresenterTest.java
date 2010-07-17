package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.analytics.SumOfMoney;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.SumOfMoneyPresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class SumOfMoneyPresenterTest {
	public static class The_Representation_Of_A_Sum_Of_Money {
		private MoneyPresenter moneyPresenter;
		private SumOfMoney sum;
		private SumOfMoneyPresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.moneyPresenter = new MoneyPresenter();
			this.presenter = new SumOfMoneyPresenter(moneyPresenter);
			
			this.sum = new SumOfMoney(money("32.54", USD), 20);
		}
		
		@Test
		public void itItHasTheGivenName() throws Exception {
			final XmlsonObject representation = presenter.present("dingo", sum, Locale.JAPAN);
			
			assertThat(representation.getName(), is("dingo"));
		}
		
		@Test
		public void itItHasTheSumsCount() throws Exception {
			final XmlsonObject representation = presenter.present("dingo", sum, Locale.JAPAN);
			
			assertThat(representation.getInteger("count"), is(20));
		}
		
		@Test
		public void itItHasTheSumAsAFloat() throws Exception {
			final XmlsonObject representation = presenter.present("dingo", sum, Locale.JAPAN);
			
			assertThat(representation.getString("value"), is("32.54"));
		}
		
		@Test
		public void itItHasTheSumAsAMonetaryAmount() throws Exception {
			final XmlsonObject representation = presenter.present("dingo", sum, Locale.JAPAN);
			
			assertThat(representation.getString("display"), is("US$32.54"));
		}
	}
}
