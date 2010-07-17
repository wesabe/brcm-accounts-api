package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.TaggedAmountPresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class TaggedAmountPresenterTest {
	private static abstract class Context {
		protected TaggedAmount taggedAmount;
		protected TaggedAmountPresenter presenter;

		public void setup() throws Exception {
			this.taggedAmount = mock(TaggedAmount.class);
			when(taggedAmount.getTag()).thenReturn(new Tag("food"));
			
			this.presenter = new TaggedAmountPresenter(new MoneyPresenter());
		}
		
	}
	
	public static class Presenting_A_Tagged_Amount extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			when(taggedAmount.isSplit()).thenReturn(false);
		}
		
		@Test
		public void itIsNamedTag() throws Exception {
			final XmlsonObject representation = presenter.present(taggedAmount, Locale.ITALY);
			assertThat(representation.getName(), is("tag"));
		}
		
		@Test
		public void itHasAUri() throws Exception {
			final XmlsonObject representation = presenter.present(taggedAmount, Locale.ITALY);
			
			assertThat(representation.getString("uri"), is("/tags/food"));
		}
	}
	
	public static class Presenting_A_Tagged_Split_Amount extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			when(taggedAmount.getAmount()).thenReturn(money("-34.40", USD));
			when(taggedAmount.isSplit()).thenReturn(true);
		}
		
		@Test
		public void itHasAnAmountInTheGivenLocale() throws Exception {
			final XmlsonObject representation = presenter.present(taggedAmount, Locale.ITALY);
			
			final XmlsonObject amount = (XmlsonObject) representation.get("amount");
			assertThat(amount.getString("display"), is("-US$ 34,40"));
			assertThat(amount.getString("value"), is("-34.40"));
		}
	}
}
