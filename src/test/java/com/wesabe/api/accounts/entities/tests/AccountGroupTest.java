package com.wesabe.api.accounts.entities.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountGroup;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.AccountType;

@RunWith(Enclosed.class)
public class AccountGroupTest {
	public static class An_Account_Group {
		private Account checking;
		private AccountGroup group;
		
		@Before
		public void setup() throws Exception {
			this.checking = mock(Account.class);
			when(checking.toString()).thenReturn("CHECKING");
			
			this.group = new AccountGroup("Checking", "checking", new AccountList(checking));
		}
		
		@Test
		public void itIsHumanReadable() throws Exception {
			assertThat(group.toString(), is("(checking:Checking [CHECKING])"));
		}
	}
	
	public static class Equality_Testing {
		private final Account checking = Account.ofType(AccountType.CHECKING);
		private final AccountGroup group = new AccountGroup("Checking", "checking", new AccountList(checking));

		@Test
		public void itEqualsAnotherAccountGroupWithTheSameNameIdAndAccounts() {
			final AccountGroup anotherGroup = new AccountGroup(group.getName(), group.getId(), group.getAccounts());
			assertThat(group.equals(anotherGroup), is(true));
			assertThat(group.hashCode(), is(anotherGroup.hashCode()));
		}
		
		@Test
		public void itDoesNotEqualAnotherAccountGroupWithADifferentName() {
			final AccountGroup anotherGroup = new AccountGroup("Not Checking", group.getId(), group.getAccounts());
			assertThat(group.equals(anotherGroup), is(false));
			assertThat(group.hashCode(), is(not(anotherGroup.hashCode())));
		}
		
		@Test
		public void itDoesNotEqualAnotherAccountGroupWithDifferentId() {
			final AccountGroup anotherGroup = new AccountGroup(group.getName(), "notchecking", group.getAccounts());
			assertThat(group.equals(anotherGroup), is(false));
			assertThat(group.hashCode(), is(not(anotherGroup.hashCode())));
		}
		
		@Test
		public void itDoesNotEqualAnotherAccountGroupWithDifferentAccounts() {
			final AccountGroup anotherGroup = new AccountGroup(group.getName(), group.getId(), new AccountList(checking, checking));
			assertThat(group.equals(anotherGroup), is(false));
			assertThat(group.hashCode(), is(not(anotherGroup.hashCode())));
		}
	}
}
