package com.wesabe.api.accounts.presenters.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Merchant;
import com.wesabe.api.accounts.presenters.MerchantReferencePresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class MerchantReferencePresenterTest {
	public static class Presenting_A_Merchant {
		private Merchant merchant;
		private MerchantReferencePresenter presenter;
		
		@Before
		public void setup() throws Exception {
			this.merchant = mock(Merchant.class);
			when(merchant.getName()).thenReturn("Starbucks");
			when(merchant.getId()).thenReturn(20);
			
			this.presenter = new MerchantReferencePresenter();
		}
		
		@Test
		public void itIsNamedMerchant() throws Exception {
			final XmlsonObject representation = presenter.present(merchant);
			
			assertThat(representation.getName(), is("merchant"));
		}
		
		@Test
		public void itHasAName() throws Exception {
			final XmlsonObject representation = presenter.present(merchant);
			
			assertThat(representation.getString("name"), is("Starbucks"));
		}
		
		@Test
		public void itHasAnId() throws Exception {
			final XmlsonObject representation = presenter.present(merchant);
			
			assertThat(representation.getInteger("id"), is(20));
		}
		
		@Test
		public void itHasAUri() throws Exception {
			final XmlsonObject representation = presenter.present(merchant);
			
			assertThat(representation.getString("uri"), is("/transactions/merchant/20"));
		}
	}
}
