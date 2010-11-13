package com.wesabe.api.accounts.presenters;

import java.util.Locale;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.inject.Inject;
import com.wesabe.api.accounts.entities.Attachment;
import com.wesabe.api.accounts.entities.TaggedAmount;
import com.wesabe.api.accounts.entities.Txaction;
import com.wesabe.xmlson.XmlsonArray;
import com.wesabe.xmlson.XmlsonObject;

/**
 * A presenter for {@link Txaction} instances.
 * 
 * @author coda
 *
 */
public class TxactionPresenter {
	private static final DateTimeFormatter ISO_BASIC = ISODateTimeFormat.basicDate();
	private final MoneyPresenter moneyPresenter;
	private final AccountBriefPresenter accountPresenter;
	private final MerchantReferencePresenter merchantPresenter;
	private final AttachmentPresenter attachmentPresenter;
	private final TaggedAmountPresenter taggedAmountPresenter;

	@Inject
	public TxactionPresenter(MoneyPresenter moneyPresenter, AccountBriefPresenter accountPresenter,
			MerchantReferencePresenter merchantPresenter, AttachmentPresenter attachmentPresenter,
			TaggedAmountPresenter taggedAmountPresenter) {
		this.moneyPresenter = moneyPresenter;
		this.accountPresenter = accountPresenter;
		this.merchantPresenter = merchantPresenter;
		this.attachmentPresenter = attachmentPresenter;
		this.taggedAmountPresenter = taggedAmountPresenter;
	}

	public XmlsonObject present(Txaction txaction, Locale locale) {
		return presentWithTransfer(txaction, locale);
	}

	private XmlsonObject presentWithTransfer(Txaction txaction, Locale locale) {
		final XmlsonObject root = presentWithoutTransfer("transaction", txaction, locale);
		
		if (txaction.isPairedTransfer()) {
			root.add(presentWithoutTransfer("transfer", txaction.getTransferTxaction(), locale));
		} else if (txaction.isTransfer()) {
			root.addProperty("transfer", true);
		} else {
			root.addNullProperty("transfer");
		}
		
		return root;
	}

	private XmlsonObject presentWithoutTransfer(String name, Txaction txaction, Locale locale) {
		final XmlsonObject root = new XmlsonObject(name);
		root.addProperty("id", txaction.getId());
		root.addProperty("uri", String.format("/transactions/%d", txaction.getId()));
		root.add(accountPresenter.present(txaction.getAccount()));
		root.addProperty("date", ISO_BASIC.print(txaction.getDatePosted()));
		root.addProperty("original-date", ISO_BASIC.print(txaction.getOriginalDatePosted()));
		root.add(moneyPresenter.present("amount", txaction.getAmount(), locale));

		if (txaction.getMerchant() != null) {
			root.add(merchantPresenter.present(txaction.getMerchant()));
		} else {
			root.addNullProperty("merchant");
		}
		
		if (txaction.getCheckNumber() != null) {
			root.addProperty("check-number", txaction.getCheckNumber());
		} else {
			root.addNullProperty("check-number");
		}
		
		if (!txaction.getAttachments().isEmpty()) {
			final XmlsonArray attachments = new XmlsonArray("attachments");
			for (Attachment attachment : txaction.getAttachments()) {
				attachments.add(attachmentPresenter.present(attachment));
			}
			root.add(attachments);
		}
		
		root.addProperty("unedited-name", txaction.getUneditedName());
		root.addProperty("note", txaction.getNote());
		
		final XmlsonArray taggedAmounts = new XmlsonArray("tags");
		for (TaggedAmount taggedAmount : txaction.getTaggedAmounts()) {
			taggedAmounts.add(taggedAmountPresenter.present(taggedAmount, locale));
		}
		root.add(taggedAmounts);
		return root;
	}
}
