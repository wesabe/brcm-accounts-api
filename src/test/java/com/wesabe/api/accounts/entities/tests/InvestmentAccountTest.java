package com.wesabe.api.accounts.entities.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.common.collect.Sets;
import com.wesabe.api.accounts.entities.InvestmentAccount;
import com.wesabe.api.accounts.entities.InvestmentPosition;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static com.wesabe.api.tests.util.InjectionHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class InvestmentAccountTest {
	public static class An_Investment_Account_With_No_Positions {
		private final InvestmentAccount account = new InvestmentAccount();
		
		@Before
		public void setup() throws Exception {
			inject(InvestmentAccount.class, account, "investmentPositions", Sets.newHashSet());
		}
		
		@Test
		public void itHasAZeroBalance() {
			assertEquals(money("0.00", USD), account.getBalance());
		}
	}
	
	public static class An_Investment_Account_With_One_Position {
		private final InvestmentAccount account = new InvestmentAccount();
		private final InvestmentPosition oneAppleShare = new InvestmentPosition();
		
		@Before
		public void setup() throws Exception {
			oneAppleShare.setAccount(account);
			inject(InvestmentPosition.class, oneAppleShare, "marketValue", decimal("100.00"));
			inject(InvestmentAccount.class, account, "investmentPositions", Sets.newHashSet(oneAppleShare));
		}
		
		@Test
		public void itHasBalanceEqualToTheMarketValueOfThePosition() {
			assertEquals(oneAppleShare.getMarketValue(), account.getBalance());
		}
	}
	
	public static class An_Investment_Account_With_Many_Positions {
		private final InvestmentAccount account = new InvestmentAccount();
		private final InvestmentPosition oneAppleShare = new InvestmentPosition();
		private final InvestmentPosition tenGoogleShares = new InvestmentPosition();
		
		@Before
		public void setup() throws Exception {
			oneAppleShare.setAccount(account);
			oneAppleShare.setUploadId(1);
			tenGoogleShares.setAccount(account);
			tenGoogleShares.setUploadId(1);
			inject(InvestmentPosition.class, oneAppleShare, "marketValue", decimal("100.00"));
			inject(InvestmentPosition.class, tenGoogleShares, "marketValue", decimal("3000.00"));
			inject(InvestmentAccount.class, account, "investmentPositions", Sets.newHashSet(oneAppleShare, tenGoogleShares));
		}
		
		@Test
		public void itHasBalanceEqualToTheSumOfTheMarketValueOfThePositions() {
			assertEquals(money("3100.00", USD), account.getBalance());
		}
	}
}
