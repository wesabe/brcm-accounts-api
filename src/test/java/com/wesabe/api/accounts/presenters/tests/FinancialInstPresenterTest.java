package com.wesabe.api.accounts.presenters.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import com.wesabe.api.accounts.entities.FinancialInst;
import com.wesabe.api.accounts.presenters.FinancialInstPresenter;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class FinancialInstPresenterTest {
	public static class Presenting_A_Financial_Inst {
		private FinancialInstPresenter presenter;
		private FinancialInst financialInst;
		
		@Before
		public void setup() throws Exception {
			this.financialInst = mock(FinancialInst.class);
			when(financialInst.getName()).thenReturn("My CU");			
			when(financialInst.getWesabeId()).thenReturn("us-123456");

			this.presenter = new FinancialInstPresenter();			
		}
		
		@Test
		public void itHasTheName() throws Exception {
			final XmlsonObject representation = presenter.present(financialInst);
			
			assertThat(representation.getString("name"), is("My CU"));
		}
		
		@Test
		public void itHasTheId() throws Exception {
			final XmlsonObject representation = presenter.present(financialInst);
			
			assertThat(representation.getString("id"), is("us-123456"));
		}
	}
}
