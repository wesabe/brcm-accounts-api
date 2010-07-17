package com.wesabe.api.accounts.entities;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.*;

import com.google.common.base.Objects;
import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.ExchangeRateNotFoundException;
import com.wesabe.api.util.money.Monetary;
import com.wesabe.api.util.money.Money;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

/**
 * An amount of money associated with a tag.
 * 
 * @author coda
 */
@Entity
@Table(name="txaction_taggings")
public class TaggedAmount implements Monetary {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer	id;
	
	/**
	 * Returns the entity's primary key.
	 * 
	 * @return the entity's primary key
	 */
	public Integer getId() {
		return id;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="txaction_id")
	private Txaction txaction;
	
	@Column(name="split_amount")
	private BigDecimal amount;
	
	@Column(name="name")
	private String tagName;
	
	public TaggedAmount() {}
	
	public TaggedAmount(Txaction txaction, Tag tag, BigDecimal amount) {
		this();
		this.txaction = txaction;
		this.amount = amount;
		this.tagName = tag.toString();
	}
	
	public Tag getTag() {
		return new Tag(tagName);
	}
	
	public Txaction getTxaction() {
		return txaction;
	}

	/* (non-Javadoc)
	 * @see com.wesabe.api.accounts.entities.Monetary#getAmount()
	 */
	@Override
	public Money getAmount() throws UnknownCurrencyCodeException {
		if (amount == null) {
			return txaction.getAmount();
		}
		return new Money(amount, txaction.getAccount().getCurrency());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.wesabe.api.accounts.entities.Monetary#getConvertedAmount(java.util.Currency, com.wesabe.api.util.money.CurrencyExchangeRateMap)
	 */
	@Override
	public Money getConvertedAmount(Currency target,
			CurrencyExchangeRateMap exchangeRates)
			throws ExchangeRateNotFoundException, UnknownCurrencyCodeException {
		return getAmount().convert(exchangeRates, target, txaction.getDatePosted());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaggedAmount) {
			TaggedAmount other = (TaggedAmount) obj;
			
			return Objects.equal(amount, other.amount)
					&& Objects.equal(getTag(), other.getTag())
					&& Objects.equal(txaction, other.txaction);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(amount, txaction, getTag());
	}

	public boolean isSplit() {
		return !txaction.getAmount().equals(getAmount());
	}

	@Override
	public String toString() {
		return "<TaggedAmount amount=" + getAmount() + ", tag=" + getTag() + ">";
	}
	
	
}
