package com.wesabe.api.accounts.presenters.tests;

import static com.wesabe.api.tests.util.CurrencyHelper.*;
import static com.wesabe.api.tests.util.MoneyHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Attachment;
import com.wesabe.api.accounts.entities.Merchant;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.presenters.AccountBriefPresenter;
import com.wesabe.api.accounts.presenters.AttachmentPresenter;
import com.wesabe.api.accounts.presenters.MerchantReferencePresenter;
import com.wesabe.api.accounts.presenters.MoneyPresenter;
import com.wesabe.api.accounts.presenters.TaggedAmountPresenter;
import com.wesabe.api.accounts.presenters.TxactionPresenter;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonElement;
import com.wesabe.xmlson.XmlsonObject;

@RunWith(Enclosed.class)
public class TxactionPresenterTest {
	private static abstract class Context {

		protected Txaction txaction;
		protected AccountBriefPresenter accountPresenter;
		protected TxactionPresenter presenter;
		protected Account account;
		protected XmlsonObject accountRepresentation;
		protected MerchantReferencePresenter merchantPresenter;
		protected AttachmentPresenter attachmentPresenter;
		protected TaggedAmountPresenter taggedAmountPresenter;

		public void setup() throws Exception {
			this.account = mock(Account.class);
			
			this.txaction = mock(Txaction.class);
			when(txaction.getId()).thenReturn(200497);
			when(txaction.getAccount()).thenReturn(account);
			when(txaction.getDatePosted()).thenReturn(new DateTime(2008, 9, 4, 3, 0, 0, 0));
			when(txaction.getOriginalDatePosted()).thenReturn(new DateTime(2008, 9, 6, 3, 0, 0, 0));
			when(txaction.getAmount()).thenReturn(money("-33.40", USD));
			when(txaction.getUneditedName()).thenReturn("BLAH");
			when(txaction.getCheckNumber()).thenReturn(null);
			when(txaction.getNote()).thenReturn("A thing.");
			when(txaction.getTaggedAmounts()).thenReturn(new ArrayList<TaggedAmount>());
			when(txaction.getAttachments()).thenReturn(new HashSet<Attachment>());
			
			this.accountRepresentation = new XmlsonObject("account");
			
			this.accountPresenter = mock(AccountBriefPresenter.class);
			when(accountPresenter.present(Mockito.any(Account.class))).thenReturn(accountRepresentation);
			
			this.merchantPresenter = mock(MerchantReferencePresenter.class);
			
			this.attachmentPresenter = mock(AttachmentPresenter.class);
			
			this.taggedAmountPresenter = mock(TaggedAmountPresenter.class);
			
			presenter = new TxactionPresenter(new MoneyPresenter(), accountPresenter, merchantPresenter, attachmentPresenter, taggedAmountPresenter);
		}
		
	}
	
	public static class The_Representation_Of_A_Txaction extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
		}
		
		@Test
		public void itIsNamedTransaction() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getName(), is("transaction"));
		}
		
		@Test
		public void itHasTheTransactionId() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getInteger("id"), is(200497));
		}
		
		@Test
		public void itHasAUri() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("uri"), is("/transactions/200497"));
		}
		
		@Test
		public void itHasABriefAccountRepresentation() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.get("account"), is((XmlsonElement) accountRepresentation));
			
			verify(accountPresenter).present(account);
		}
		
		@Test
		public void itHasTheDatePosted() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("date"), is("20080904"));
		}
		
		@Test
		public void itHasTheOriginalDatePosted() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("original-date"), is("20080906"));
		}
		
		@Test
		public void itHasTheAmount() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			final XmlsonObject amount = (XmlsonObject) representation.get("amount");
			assertThat(amount.getString("display"), is("-$33.40"));
			assertThat(amount.getString("value"), is("-33.40"));
		}
		
		@Test
		public void itHasANullMerchant() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("merchant"), is(nullValue()));
		}
		
		@Test
		public void itHasANullCheckNumber() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("check-number"), is(nullValue()));
		}
		
		@Test
		public void itHasANullTransfer() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("transfer"), is(nullValue()));
		}
		
		@Test
		public void itHasAnUneditedName() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("unedited-name"), is("BLAH"));
		}
		
		@Test
		public void itHasANote() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("note"), is("A thing."));
		}
				
		@Test
		public void itHasAnEmptyArrayOfTags() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			final XmlsonArray tags = (XmlsonArray) representation.get("tags");
			assertThat(tags.getMembers().size(), is(0));
		}
	}
	
	public static class The_Representation_Of_A_Txaction_With_A_Check_Number extends Context {
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			when(txaction.getCheckNumber()).thenReturn("1070");
		}
		
		@Test
		public void itHasACheckNumber() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat(representation.getString("check-number"), is("1070"));
		}
	}
	
	public static class The_Representation_Of_A_Txaction_With_A_Merchant extends Context {
		private Merchant merchant;
		private XmlsonObject merchantRepresentation;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.merchant = mock(Merchant.class);
			this.merchantRepresentation = new XmlsonObject("merchant");
			when(merchantPresenter.present(Mockito.any(Merchant.class))).thenReturn(merchantRepresentation);
			
			when(txaction.getMerchant()).thenReturn(merchant);
		}
		
		@Test
		public void itHasAMerchant() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			assertThat((XmlsonObject) representation.get("merchant"), is(merchantRepresentation));
			
			verify(merchantPresenter).present(merchant);
		}
	}
	
	public static class The_Representation_Of_A_Txaction_With_An_Attachment extends Context {
		private Attachment attachment;
		private XmlsonObject attachmentRepresentation;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.attachment = mock(Attachment.class);
			this.attachmentRepresentation = new XmlsonObject("attachment");
			when(attachmentPresenter.present(Mockito.any(Attachment.class))).thenReturn(attachmentRepresentation);
			
			when(txaction.getAttachments()).thenReturn(ImmutableSet.of(attachment));
		}
		
		@Test
		public void itHasAnAttachment() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);
			
			final XmlsonArray attachments = (XmlsonArray) representation.get("attachments");
			assertThat(attachments.getMembers().size(), is(1));
			
			final XmlsonObject attachment = (XmlsonObject) attachments.getMembers().get(0);
			assertThat(attachment, is(attachmentRepresentation));
			
			verify(attachmentPresenter).present(this.attachment);
		}
	}
	
	public static class The_Representation_Of_A_Txaction_With_Tagged_Amounts extends Context {
		private TaggedAmount taggedAmount;
		private XmlsonObject taggedAmountRepresentation;

		@Before
		@Override
		public void setup() throws Exception {
			super.setup();

			this.taggedAmount = mock(TaggedAmount.class);
			this.taggedAmountRepresentation = new XmlsonObject("tag");
			when(taggedAmountPresenter.present(Mockito.any(TaggedAmount.class), Mockito.any(Locale.class))).thenReturn(taggedAmountRepresentation);

			when(txaction.getTaggedAmounts()).thenReturn(ImmutableList.of(taggedAmount));
		}

		@Test
		public void itHasAnArrayOfTaggedAmounts() throws Exception {
			final XmlsonObject representation = presenter.present(txaction, Locale.UK);

			final XmlsonArray tags = (XmlsonArray) representation.get("tags");
			assertThat(tags.getMembers().size(), is(1));

			final XmlsonObject tag = (XmlsonObject) tags.getMembers().get(0);
			assertThat(tag, is(taggedAmountRepresentation));

			verify(taggedAmountPresenter).present(this.taggedAmount, Locale.UK);
		}
	}
	
	public static class The_Representation_Of_A_Transfer_Txaction extends Context {
		private Txaction transfer;
		
		@Before
		@Override
		public void setup() throws Exception {
			super.setup();
			
			this.transfer = mock(Txaction.class);
			when(transfer.getId()).thenReturn(200498);
			when(transfer.getAccount()).thenReturn(account);
			when(transfer.getDatePosted()).thenReturn(new DateTime(2008, 9, 5, 12, 0, 0, 0));
			when(transfer.getOriginalDatePosted()).thenReturn(new DateTime(2008, 9, 8, 6, 0, 0, 0));
			when(transfer.getAmount()).thenReturn(money("33.40", USD));
			when(transfer.getUneditedName()).thenReturn("BLAH");
			when(transfer.getTaggedAmounts()).thenReturn(new ArrayList<TaggedAmount>());
			when(transfer.getAttachments()).thenReturn(new HashSet<Attachment>());
			when(transfer.isTransfer()).thenReturn(true);
			when(transfer.getTransferTxaction()).thenReturn(txaction);
			when(transfer.getNote()).thenReturn("Another thing.");
			when(txaction.isTransfer()).thenReturn(true);
			when(txaction.getTransferTxaction()).thenReturn(transfer);
		}
		
		@Test
		public void itHasTheTransactionId() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.getInteger("id"), is(200498));
		}
		
		@Test
		public void itHasAUri() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.getString("uri"), is("/transactions/200498"));
		}
		
		@Test
		public void itHasABriefAccountRepresentation() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.get("account"), is((XmlsonElement) accountRepresentation));
			
			verify(accountPresenter, atLeastOnce()).present(account);
		}
		
		@Test
		public void itHasTheDatePosted() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.getString("date"), is("20080905"));
		}
		
		@Test
		public void itHasTheOriginalDatePosted() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.getString("original-date"), is("20080908"));
		}
		
		@Test
		public void itHasTheAmount() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			final XmlsonObject amount = (XmlsonObject) representation.get("amount");
			assertThat(amount.getString("display"), is("$33.40"));
			assertThat(amount.getString("value"), is("33.40"));
		}
		
		@Test
		public void itHasAnUneditedName() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.getString("unedited-name"), is("BLAH"));
		}
		
		@Test
		public void itHasANote() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			assertThat(representation.getString("note"), is("Another thing."));
		}
		
		@Test
		public void itHasAnEmptyArrayOfTags() throws Exception {
			final XmlsonObject envelope = presenter.present(txaction, Locale.UK);
			final XmlsonObject representation = (XmlsonObject) envelope.get("transfer");
			
			final XmlsonArray tags = (XmlsonArray) representation.get("tags");
			assertThat(tags.getMembers().size(), is(0));
		}
	}
}
