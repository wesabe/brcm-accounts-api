package com.wesabe.api.accounts.entities.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import com.wesabe.api.accounts.entities.AccountType;

@RunWith(Enclosed.class)
public class AccountTypeTest {
	public static class UnknownAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.UNKNOWN;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Unknown", accountType.toString());
		}
		
		@Test
		public void itSerializesToOne() {
			assertEquals(1, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromOne() {
			assertEquals(accountType, AccountType.byValue(1));
		}
	}
	
	public static class CheckingAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.CHECKING;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Checking", accountType.toString());
		}
		
		@Test
		public void itSerializesToTwo() {
			assertEquals(2, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromTwo() {
			assertEquals(accountType, AccountType.byValue(2));
		}
	}
	
	public static class MoneyMarketAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.MONEY_MARKET;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Money Market", accountType.toString());
		}
		
		@Test
		public void itSerializesToThree() {
			assertEquals(3, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromThree() {
			assertEquals(accountType, AccountType.byValue(3));
		}
	}
	
	public static class CreditCardAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.CREDIT_CARD;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Credit Card", accountType.toString());
		}
		
		@Test
		public void itSerializesToFour() {
			assertEquals(4, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromFour() {
			assertEquals(accountType, AccountType.byValue(4));
		}
	}
	
	public static class SavingsAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.SAVINGS;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Savings", accountType.toString());
		}
		
		@Test
		public void itSerializesToFive() {
			assertEquals(5, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromFive() {
			assertEquals(accountType, AccountType.byValue(5));
		}
	}
	
	public static class CreditLineAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.CREDIT_LINE;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Credit Line", accountType.toString());
		}
		
		@Test
		public void itSerializesToSeven() {
			assertEquals(7, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromSeven() {
			assertEquals(accountType, AccountType.byValue(7));
		}
	}
	
	public static class BrokerageAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.BROKERAGE;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Brokerage", accountType.toString());
		}
		
		@Test
		public void itSerializesToEight() {
			assertEquals(8, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromEight() {
			assertEquals(accountType, AccountType.byValue(8));
		}
	}
	
	public static class CashAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.CASH;
		}
		
		@Test
		public void itDoesNotHaveABalance() {
			assertFalse(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Cash", accountType.toString());
		}
		
		@Test
		public void itSerializesToNine() {
			assertEquals(9, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromNine() {
			assertEquals(accountType, AccountType.byValue(9));
		}
	}
	
	public static class ManualAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.MANUAL;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itHasAContinuousBalance() {
			assert(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Manual", accountType.toString());
		}
		
		@Test
		public void itSerializesToTen() {
			assertEquals(10, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromTen() {
			assertEquals(accountType, AccountType.byValue(10));
		}
	}
	
	public static class InvestmentAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.INVESTMENT;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Investment", accountType.toString());
		}
		
		@Test
		public void itSerializesToEleven() {
			assertEquals(11, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromEleven() {
			assertEquals(accountType, AccountType.byValue(11));
		}
	}
	
	public static class CertificateAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.CERTIFICATE;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Certificate of Deposit", accountType.toString());
		}
		
		@Test
		public void itSerializesToTwelve() {
			assertEquals(12, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromTwelve() {
			assertEquals(accountType, AccountType.byValue(12));
		}
	}

	public static class LoanAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.LOAN;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Loan", accountType.toString());
		}
		
		@Test
		public void itSerializesToThirteen() {
			assertEquals(13, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromThirteen() {
			assertEquals(accountType, AccountType.byValue(13));
		}
	}
	
	public static class MortgageAccountType {
		private AccountType accountType;

		@Before
		public void setup() {
			this.accountType = AccountType.MORTGAGE;
		}
		
		@Test
		public void itHasABalance() {
			assert(accountType.hasBalance());
		}
		
		@Test
		public void itDoesNotHaveAContinuousBalance() {
			assertFalse(accountType.hasContinuousBalance());
		}
		
		@Test
		public void itIsHumanReadable() {
			assertEquals("Mortgage", accountType.toString());
		}
		
		@Test
		public void itSerializesToFourteen() {
			assertEquals(14, accountType.getValue());
		}
		
		@Test
		public void itDeserializesFromFourteen() {
			assertEquals(accountType, AccountType.byValue(14));
		}
	}	
}
