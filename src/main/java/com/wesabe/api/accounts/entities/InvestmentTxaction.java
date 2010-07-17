package com.wesabe.api.accounts.entities;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.wesabe.api.util.money.CurrencyExchangeRateMap;
import com.wesabe.api.util.money.ExchangeRateNotFoundException;
import com.wesabe.api.util.money.Monetary;
import com.wesabe.api.util.money.Money;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

@Entity
@Table(name="investment_txactions")
@NamedQueries({
	@NamedQuery(
		name  = "com.wesabe.api.accounts.InvestmentTxaction.findInAccounts",
		query = "SELECT t FROM InvestmentTxaction t" +
				" LEFT OUTER JOIN FETCH t.investmentSecurity AS s" +
				" WHERE t.account IN (:accounts)" +
				" ORDER BY t.tradeDate ASC, t.createdAt ASC"
	),
	@NamedQuery(
		name  = "com.wesabe.api.accounts.InvestmentTxaction.findMostRecentInAccounts",
		query = "SELECT t FROM InvestmentTxaction t" +
				" LEFT OUTER JOIN FETCH t.investmentSecurity AS s" +
				" WHERE t.account IN (:accounts)" +
				" ORDER BY t.tradeDate DESC, t.createdAt DESC"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.InvestmentTxaction.findFirstTradeDate",
		query = "SELECT MIN(t.tradeDate) FROM InvestmentTxaction t" +
				" WHERE t.account IN (:accounts)"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.InvestmentTxaction.findInDateRange",
		query = "SELECT t FROM InvestmentTxaction t" +
				" LEFT OUTER JOIN FETCH t.investmentSecurity AS s" +
				" WHERE t.account IN (:accounts) AND t.tradeDate >= :startDate AND t.tradeDate < :endDate" +
				" ORDER BY t.tradeDate ASC, t.createdAt ASC"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.InvestmentTxaction.findBeforeDate",
		query = "SELECT t FROM InvestmentTxaction t" +
				" LEFT OUTER JOIN FETCH t.investmentSecurity AS s" +
				" WHERE t.account IN (:accounts) AND t.tradeDate < :endDate" +
				" ORDER BY t.tradeDate ASC, t.createdAt ASC"
	),
	@NamedQuery(
		name = "com.wesabe.api.accounts.InvestmentTxaction.findAfterDate",
		query = "SELECT t FROM InvestmentTxaction t" +
				" LEFT OUTER JOIN FETCH t.investmentSecurity AS s" +
				" WHERE t.account IN (:accounts) AND t.tradeDate >= :startDate" +
				" ORDER BY t.tradeDate ASC, t.createdAt ASC"
	)
})
public class InvestmentTxaction implements Monetary, Comparable<InvestmentTxaction> {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id")
	private Account account;

	@Column(name="trade_date")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime tradeDate;

	@Column
	private String memo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="investment_security_id")
	private InvestmentSecurity investmentSecurity;

	@Column
	private BigDecimal units;
	
	@Column(name="unit_price")
	private BigDecimal unitPrice;

	@Column
	private BigDecimal total;

	@Column(name="created_at")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime createdAt;
	
	@Column
	private Integer status = TxactionStatus.ACTIVE.getValue();

	public InvestmentTxaction() {
	}

	public InvestmentTxaction(Account account, BigDecimal total, DateTime tradeDate) {
		this.account = account;
		this.total = total;
		this.tradeDate = tradeDate;
	}

	public int getId() {
		return id;
	}
	
	public DateTime getTradeDate() {
		return tradeDate;
	}
	
	public void setTradeDate(DateTime tradeDate) {
		this.tradeDate = tradeDate;
	}
	
	public String getMemo() {
		return this.memo;		
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public BigDecimal getUnits() {
		return this.units;
	}
	
	public void setUnits(BigDecimal units) {
		this.units = units;
	}

	public Money getUnitPrice() throws UnknownCurrencyCodeException {
		return new Money(unitPrice, account.getCurrency());
	}
	
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public InvestmentSecurity getInvestmentSecurity() {
		return investmentSecurity;
	}
	
	public void setInvestmentSecurity(InvestmentSecurity security) {
		this.investmentSecurity = security;
	}
	
	public DateTime getCreatedAt() {
		return createdAt;
	}
		
	public TxactionStatus getStatus() {
		return TxactionStatus.byValue(status);
	}

	public void setStatus(TxactionStatus status) {
		this.status = Integer.valueOf(status.getValue());
	}
	
	public boolean isDeleted() {
		return getStatus() == TxactionStatus.DELETED;
	}
	
	public boolean isDisabled() {
		return getStatus() == TxactionStatus.DISABLED;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.wesabe.api.accounts.entities.Monetary#getAmount()
	 */
	@Override
	public Money getAmount() throws UnknownCurrencyCodeException {
		if (total != null) {
			return new Money(total, account.getCurrency());
		} else {
			return null;
		}
	}
	
	public Money getTotal() throws UnknownCurrencyCodeException {
		return getAmount();
	}

	/*
	 * (non-Javadoc)
	 * @see com.wesabe.api.accounts.entities.Monetary#getConvertedAmount(java.util.Currency, com.wesabe.api.util.money.CurrencyExchangeRateMap)
	 */
	@Override
	public Money getConvertedAmount(Currency target,
			CurrencyExchangeRateMap exchangeRates)
			throws ExchangeRateNotFoundException, UnknownCurrencyCodeException {
		return getAmount().convert(exchangeRates, target, tradeDate);
	}

	public void setAmount(BigDecimal amount) {
		setTotal(amount);
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	@Override
	public int compareTo(InvestmentTxaction other) {
	    final int equal = 0;
		
		int result = getTradeDate().compareTo(other.getTradeDate());
		
		if ((result == equal) && (getCreatedAt() != null) && (other.getCreatedAt() != null)) {
			result = getCreatedAt().compareTo(other.getCreatedAt());
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "<InvestmentTxaction total=" + total + ", security=" + investmentSecurity + ">";
	}
}
