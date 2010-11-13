package com.wesabe.api.accounts.entities;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.ExchangeRateNotFoundException;
import com.wesabe.api.util.money.Monetary;
import com.wesabe.api.util.money.Money;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

// REVIEW coda@wesabe.com -- Dec 20, 2008: Unify amount getters and setters.
// Because a Txaction's currency is that of its account, setting the amount
// of a Txaction cannot change its currency. Right now the setter accepts
// a BigDecimal, which is fine since the only writing we do here is in
// tests, but once BRCM gets over the read-only hump, this will start to be
// more important. I'd like this to have a consistent interface, and use
// Money instances, perhaps throwing a CurrencyMismatchException if things
// don't line up right.

@Entity
@Table(name="txactions")
@NamedQueries({
	@NamedQuery(
		name  = "com.wesabe.api.accounts.Txaction.findInAccounts",
		query = "SELECT t FROM Txaction t" +
				" LEFT OUTER JOIN FETCH t.merchant AS m" +
				" WHERE t.account IN (:accounts)" +
				" ORDER BY t.datePosted ASC, t.sequence DESC, t.createdAt ASC"
	),
	@NamedQuery(
		name  = "com.wesabe.api.accounts.Txaction.findMostRecentInAccounts",
		query = "SELECT t FROM Txaction t" +
				" LEFT OUTER JOIN FETCH t.merchant AS m" +
				" WHERE t.account IN (:accounts)" +
				" ORDER BY t.datePosted DESC, t.sequence ASC, t.createdAt DESC"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.Txaction.findFirstDatePosted",
		query = "SELECT MIN(t.datePosted) FROM Txaction t" +
				" WHERE t.account IN (:accounts)"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.Txaction.findInDateRange",
		query = "SELECT t FROM Txaction t" +
				" LEFT OUTER JOIN FETCH t.merchant AS m" +
				" WHERE t.account IN (:accounts) AND t.datePosted >= :startDate AND t.datePosted < :endDate" +
				" ORDER BY t.datePosted ASC, t.sequence DESC, t.createdAt ASC"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.Txaction.findBeforeDate",
		query = "SELECT t FROM Txaction t" +
				" LEFT OUTER JOIN FETCH t.merchant AS m" +
				" WHERE t.account IN (:accounts) AND t.datePosted < :endDate" +
				" ORDER BY t.datePosted ASC, t.sequence DESC, t.createdAt ASC"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.Txaction.findAfterDate",
		query = "SELECT t FROM Txaction t" +
				" LEFT OUTER JOIN FETCH t.merchant AS m" +
				" WHERE t.account IN (:accounts) AND t.datePosted >= :startDate" +
				" ORDER BY t.datePosted ASC, t.sequence DESC, t.createdAt ASC"
	)
})
public class Txaction implements Monetary, Comparable<Txaction> {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column(nullable=false)
	private BigDecimal amount;

	@Column(name="date_posted")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime datePosted;
	
	@Column(name="fi_date_posted")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime originalDatePosted;

	@Column(name="created_at")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime createdAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="transfer_txaction_id", nullable=true)
	@NotFound(action=NotFoundAction.IGNORE)
	// FIXME coda@wesabe.com -- Apr 29, 2009: Make txactions.transfer_txaction_id a foreign key.
	// It hurts my brain that we have to tell our Hibernate to not complain when
	// we try to load things. Oy.
	private Txaction transferTxaction;

	@Column(nullable=true)
	private Integer sequence;
	
	@Column
	private Integer status = TxactionStatus.ACTIVE.getValue();
	
	@Column(name="raw_name")
	private String rawName;
	
	@Column
	private String memo;
	
	@Column
	private String note;
	
	@Column
	private boolean tagged;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id")
	private Account account;
	
	@OneToMany(mappedBy="txaction")
	@BatchSize(size=200)
	@OrderBy("id")
	private List<TaggedAmount> taggedAmounts = Lists.newLinkedList();
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="merchant_id")
	private Merchant merchant;
	
	@Column(name="check_num")
	private String checkNumber;
	
	@ManyToMany
	@JoinTable(
		name="txaction_attachments",
		joinColumns=@JoinColumn(name="txaction_id"),
		inverseJoinColumns=@JoinColumn(name="attachment_id")
	)
	@BatchSize(size=200)
	private Set<Attachment> attachments = Sets.newHashSet();

	public Txaction() {
	}

	public Txaction(Account account, BigDecimal amount, DateTime datePosted) {
		this.account = account;
		this.amount = amount;
		this.datePosted = datePosted;
		this.originalDatePosted = datePosted;
	}

	public int getId() {
		return id;
	}
	
	public DateTime getDatePosted() {
		return datePosted;
	}
	
	public DateTime getOriginalDatePosted() {
		return originalDatePosted;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	public boolean isTagged() {
		return tagged;
	}
	
	public void setTagged(boolean flag) {
		this.tagged = flag;
	}
	
	public DateTime getCreatedAt() {
		return createdAt;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.wesabe.api.accounts.entities.Monetary#getAmount()
	 */
	@Override
	public Money getAmount() throws UnknownCurrencyCodeException {
		return new Money(amount, account.getCurrency());
	}

	/*
	 * (non-Javadoc)
	 * @see com.wesabe.api.accounts.entities.Monetary#getConvertedAmount(java.util.Currency, com.wesabe.api.util.money.CurrencyExchangeRateMap)
	 */
	@Override
	public Money getConvertedAmount(Currency target,
			CurrencyExchangeRateMap exchangeRates)
			throws ExchangeRateNotFoundException, UnknownCurrencyCodeException {
		return getAmount().convert(exchangeRates, target, datePosted);
	}
	
	public Txaction getTransferTxaction() {
		return transferTxaction;
	}

	public void setTransferTxaction(Txaction transferTxaction) {
		this.transferTxaction = transferTxaction;
	}

	public boolean isTransfer() {
		return transferTxaction != null;
	}
	
	public boolean isPairedTransfer() {
		return transferTxaction != null && !transferTxaction.equals(this);
	}
	
	public TxactionStatus getStatus() {
		return TxactionStatus.byValue(status);
	}
	
	public boolean isDeleted() {
		return getStatus() == TxactionStatus.DELETED;
	}
	
	public void setStatus(TxactionStatus status) {
		this.status = Integer.valueOf(status.getValue());
	}

	public List<TaggedAmount> getTaggedAmounts() {
		return taggedAmounts;
	}

	public Money getAmountByFilteringTags(Set<Tag> filteredTags) {
		final Money txactionAmount = getAmount().abs();
		Money filteredAmount = new Money(BigDecimal.ZERO, account.getCurrency());
		for (TaggedAmount taggedAmount : taggedAmounts) {
			if (filteredTags.contains(taggedAmount.getTag())) {
				filteredAmount = filteredAmount.add(taggedAmount.getAmount().abs());
			}
		}
		
		if (txactionAmount.compareTo(filteredAmount) <= 0) {
			return new Money(BigDecimal.ZERO, account.getCurrency());
		}
		return txactionAmount.subtract(filteredAmount).multiply(getAmount().signum());
	}
	
	public Money getConvertedAmountByFilteringTags(Set<Tag> filteredTags, Currency target,
			CurrencyExchangeRateMap exchangeRates) throws ExchangeRateNotFoundException,
			UnknownCurrencyCodeException {
		return getAmountByFilteringTags(filteredTags).convert(exchangeRates, target, datePosted);
	}
	
	public TaggedAmount addTag(Tag tag) {
		return addTag(tag, null);
	}
	
	public TaggedAmount addTag(Tag tag, BigDecimal amount) {
		final TaggedAmount taggedAmount = new TaggedAmount(this, tag, amount);
		taggedAmounts.add(taggedAmount);
		return taggedAmount;
	}

	public boolean isDisabled() {
		return getStatus() == TxactionStatus.DISABLED;
	}
	
	public boolean isUnedited() {
		return (getMerchant() == null) || (!isTagged() && !isTransfer());
	}
	
	@Override
	public int compareTo(Txaction other) {
	    final int equal = 0;
		
		int result = getDatePosted().compareTo(other.getDatePosted());
		
		if ((result == equal) && (getSequence() != null) && (other.getSequence() != null)) {
			result = other.getSequence().compareTo(getSequence());
		}
		
		if ((result == equal) && (getCreatedAt() != null) && (other.getCreatedAt() != null)) {
			result = getCreatedAt().compareTo(other.getCreatedAt());
		}
		
		return result;
	}

	public String getUneditedName() {
		String uneditedName = "";
		if ((rawName != null) && (rawName.trim().length() != 0)) {
			uneditedName += rawName;
		}
		if ((memo != null) && (memo.trim().length() != 0)) {
			uneditedName += (uneditedName.length() == 0 ? "" : " / ") + memo;
		}
		return uneditedName;
	}
	
	public Merchant getMerchant() {
		return merchant;
	}
	
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	
	public String getCheckNumber() {
		return checkNumber;
	}
	
	public String getNote() {
		return note;
	}
	
	public Set<Attachment> getAttachments() {
		return attachments;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "<Txaction amount=" + amount + ", merchant=" + merchant + ", taggedAmounts="
			+ taggedAmounts + ">";
	}

	public void setDatePosted(DateTime datePosted) {
		this.datePosted = datePosted;
	}
}
