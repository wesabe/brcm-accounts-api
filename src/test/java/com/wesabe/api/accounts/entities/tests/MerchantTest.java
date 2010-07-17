package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Merchant;

@RunWith(Enclosed.class)
public class MerchantTest {
	public static class A_Merchant {
		private Merchant starbucks = new Merchant("Starbucks");
		
		@Test
		public void itHasAName() {
			assertEquals("Starbucks", starbucks.getName());
		}
	}
}
