package com.wesabe.api.tests.util;

import static com.wesabe.api.tests.util.DateHelper.*;
import static com.wesabe.api.tests.util.TagHelper.*;

import java.math.BigDecimal;

import com.wesabe.api.accounts.entities.Account;
import com.wesabe.api.accounts.entities.Merchant;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.api.accounts.entities.TxactionStatus;

public class TxactionHelper {
	public static class TxactionBuilder {
		private Txaction txaction;
		
		private TxactionBuilder(Txaction txaction) {
			this.txaction = txaction;
		}
		
		public Txaction build() {
			if (txaction.getDatePosted() == null) {
				txaction.setDatePosted(now());
			}
			return txaction;
		}
		
		public TxactionBuilder from(Account account) {
			txaction.setAccount(account);
			return this;
		}
		
		public TxactionBuilder spent(String amount) {
			txaction.setAmount(new BigDecimal(amount).abs().negate());
			return this;
		}

		public TxactionBuilder at(String merchantName) {
			final Merchant merchant = new Merchant(merchantName);
			merchant.setId(merchantName.hashCode());
			txaction.setMerchant(merchant);
			return this;
		}

		public TxactionBuilder on(String... tagNames) {
			txaction.setTagged(true);
			for (String tagName : tagNames) {
				txaction.addTag(tag(tagName));
			}
			
			return this;
		}

		public TxactionBuilder on(String tagName, BigDecimal amount) {
			txaction.setTagged(true);
			txaction.addTag(tag(tagName), amount);
			return this;
		}

		public TxactionBuilder asDeleted() {
			txaction.setStatus(TxactionStatus.DELETED);
			return this;
		}

		public TxactionBuilder asTransfer() {
			txaction.setTransferTxaction(txaction);
			return this;
		}

		public TxactionBuilder asDisabled() {
			txaction.setStatus(TxactionStatus.DISABLED);
			return this;
		}
	}
	
	public static TxactionBuilder from(Account account) {
		return new TxactionBuilder(new Txaction()).from(account);
	}
	
	public static TxactionBuilder spent(String amount) {
		return new TxactionBuilder(new Txaction()).spent(amount);
	}
}
