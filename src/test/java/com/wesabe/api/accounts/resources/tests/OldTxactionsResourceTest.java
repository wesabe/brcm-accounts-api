package com.wesabe.api.accounts.resources.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.NumberHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableSet;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.AccountList;
import com.wesabe.api.accounts.entities.Tag;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionList;
import com.wesabe.api.accounts.params.BooleanParam;
import com.wesabe.api.accounts.params.CurrencyParam;
import com.wesabe.api.accounts.params.ISODateParam;
import com.wesabe.api.accounts.params.IntegerParam;
import com.wesabe.api.accounts.params.UriParam;
import com.wesabe.api.accounts.resources.OldTxactionsResource;
import com.wesabe.api.tests.util.MockResourceContext;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class OldTxactionsResourceTest {
	private static abstract class Context {
		protected MockResourceContext context;
		protected OldTxactionsResource resource;
		
		protected CurrencyParam currency;
		protected BooleanParam uneditedOnly;
		protected IntegerParam limit;
		protected IntegerParam offset;
		protected ISODateParam startDate;
		protected ISODateParam endDate;
		protected Set<UriParam> accountUris;
		protected Set<String> tagUris;
		protected Set<String> merchantNames;
		protected BigDecimal amount;
		
		protected AccountList accounts;
		protected List<Txaction> txactions;
		protected TxactionList txactionList;
		protected XmlsonObject representation;

		@SuppressWarnings("unchecked")
		public void setup() throws Exception {
			this.context = new MockResourceContext();
			this.resource = context.getInstance(OldTxactionsResource.class);
			
			this.currency = new CurrencyParam("GBP");
			this.uneditedOnly = new BooleanParam("false");
			this.limit = null;
			this.offset = null;
			this.startDate = null;
			this.endDate = null;
			this.amount = null;
			this.accountUris = ImmutableSet.of();
			this.tagUris = ImmutableSet.of();
			this.merchantNames = ImmutableSet.of();
			
			this.accounts = mock(AccountList.class);
			when(context.getAccountDAO().findVisibleAccounts(anyString())).thenReturn(accounts);
			
			this.txactions = mock(List.class);
			when(context.getTxactionDAO().findTxactions(anyCollection())).thenReturn(txactions);
			
			this.txactionList = mock(TxactionList.class);
			when(context.getTxactionListBuilder().build(anyList())).thenReturn(txactionList);
			
			this.representation = mock(XmlsonObject.class);
			when(context.getTxactionListPresenter().present(Mockito.any(TxactionList.class), Mockito.any(Locale.class))).thenReturn(representation);
		}
		
	}
	
	public static class Building_A_Transactions_List extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itLoadsTheUsersAccounts() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getAccountDAO()).findVisibleAccounts(context.getUser().getAccountKey());
		}
		
		@Test
		public void itLoadsAllTransactionsForTheAccounts() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionDAO()).findTxactions(accounts);
		}
		
		@Test
		public void itBuildsATransactionList() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilderProvider()).get();
			verify(context.getTxactionListBuilder()).setCurrency(GBP);
			verify(context.getTxactionListBuilder()).setUnedited(false);
			verify(context.getTxactionListBuilder()).setTags(ImmutableSet.<Tag>of());
			verify(context.getTxactionListBuilder()).setAccounts(accounts);
			verify(context.getTxactionListBuilder()).build(txactions);
		}
		
		@Test
		public void itPresentsTheTxactionListAsXmlson() throws Exception {
			final XmlsonObject result = resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListPresenter()).present(txactionList, Locale.CANADA_FRENCH);
			
			assertThat(result, is(representation));
		}
	}
	
	public static class Building_A_Transactions_List_Limited_To_A_Set_Of_Merchants extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.merchantNames = ImmutableSet.of("McGee's");
		}
		
		@Test
		public void itLimitsTheTransactionListToThoseMerchants() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setMerchantNames(merchantNames);
		}
	}
	
	public static class Building_A_Transactions_List_With_An_Offset extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.offset = new IntegerParam("200");
		}
		
		@Test
		public void itOffsetsTheTransactionListByTheGivenNumber() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setOffset(200);
		}
	}
	
	public static class Building_A_Transactions_List_With_A_Limit extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.limit = new IntegerParam("10");
		}
		
		@Test
		public void itLimitsTheTransactionListToTheGivenNumber() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setLimit(10);
		}
	}
	
	public static class Building_A_Transactions_List_Limited_To_A_Set_Of_Tags extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.tagUris = ImmutableSet.of("/tags/food");
		}
		
		@Test
		public void itLimitsTheTransactionListToThoseTags() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setTags(ImmutableSet.of(new Tag("food")));
		}
	}
	
	public static class Building_A_Transactions_List_Limited_To_A_Set_Of_Tags_With_Slashes_In_Them extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.tagUris = ImmutableSet.of("/tags/either/or");
		}
		
		@Test
		public void itLimitsTheTransactionListToThoseTags() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setTags(ImmutableSet.of(new Tag("either/or")));
		}
	}
	
	public static class Building_A_Transactions_List_With_A_Bad_Tag_URI extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.tagUris = ImmutableSet.of("/tagz-yo/food", "/tags/food");
		}
		
		@Test
		public void itIgnoresMalformedTagURIs() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setTags(ImmutableSet.of(new Tag("food")));
		}
	}
	
	public static class Building_A_Transactions_List_Limited_To_A_Set_Of_Accounts extends Context {
		private Account checking, savings, weirdOne;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			checking.setRelativeId(2);
			
			this.savings = new Account("Savings", USD);
			savings.setRelativeId(1);
			
			this.weirdOne = new Account("Brian Has Insane Accounts", USD);
			weirdOne.setRelativeId(400);
			
			when(context.getAccountDAO().findVisibleAccounts(anyString())).thenReturn(new AccountList(checking, savings, weirdOne));
			
			this.accountUris = ImmutableSet.of(new UriParam("/accounts/2"), new UriParam("/accounts/400"));
		}
		
		@Test
		public void itLimitsTheTransactionListToThoseAccounts() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setAccounts(new AccountList(checking, weirdOne));
		}
	}
	
	public static class Building_A_Transactions_List_With_Bad_Account_URIs extends Context {
		private Account checking, savings;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.checking = new Account("Checking", USD);
			checking.setRelativeId(2);
			
			this.savings = new Account("Savings", USD);
			savings.setRelativeId(1);
			
			when(context.getAccountDAO().findVisibleAccounts(anyString())).thenReturn(new AccountList(checking, savings));
			
			this.accountUris = ImmutableSet.of(new UriParam("/accounts/2"), new UriParam("/accounts/woof"), new UriParam("/accouFFFnts/2"));
		}
		
		@Test
		public void itIgnoresMalformedAccountURIs() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setAccounts(new AccountList(checking));
		}
	}
	
	public static class Building_A_Transactions_List_With_A_Start_Date extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.startDate = new ISODateParam("20060809");
		}
		
		@Test
		public void itLimitsTheTransactionListToTxactionsAfterTheStartDate() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionDAO()).findTxactionsAfterDate(accounts, date(2006, 8, 9));
		}
	}
	
	public static class Building_A_Transactions_List_With_An_End_Date extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.endDate = new ISODateParam("20060909");
		}
		
		@Test
		public void itLimitsTheTransactionListToTxactionsBeforeTheEndDate() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionDAO()).findTxactionsBeforeDate(accounts, date(2006, 9, 9));
		}
	}
	
	public static class Building_A_Transactions_List_With_A_Start_Date_And_An_End_Date extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.startDate = new ISODateParam("20060809");
			this.endDate = new ISODateParam("20060909");
		}
		
		@Test
		public void itLimitsTheTransactionListToTxactionsBeforeTheEndDate() throws Exception {
			resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionDAO()).findTxactionsInDateRange(accounts, new Interval(date(2006, 8, 9), date(2006, 9, 9)));
		}
	}
	
	public static class Building_A_Transactions_List_With_A_Bad_Interval extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.startDate = new ISODateParam("20070809");
			this.endDate = new ISODateParam("20060909");
		}
		
		@Test
		public void itReturns400BadRequest() throws Exception {
			try {
				resource.show(context.getUser(), Locale.CANADA_FRENCH, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
				fail("should have raised a WebApplicationException but didn't");
			} catch (WebApplicationException e) {
				assertThat(e.getResponse().getStatus(), is(400));
			}
		}
	}
	
	public static class Building_A_Transaction_List_With_A_Specific_Amount extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			this.amount = decimal("-20.00");
		}
		
		@Test
		public void itFindsOnlyTransactionsWithTheGivenAmount() throws Exception {
			resource.show(context.getUser(), Locale.CANADA, currency, uneditedOnly, limit, offset, startDate, endDate, accountUris, tagUris, merchantNames, amount);
			
			verify(context.getTxactionListBuilder()).setAmount(amount);
		}
	}
}
