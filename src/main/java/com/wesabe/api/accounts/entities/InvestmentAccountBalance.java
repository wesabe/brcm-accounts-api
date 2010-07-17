package com.wesabe.api.accounts.entities;

import java.math.BigDecimal;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.wesabe.api.util.money.Money;

@Entity
@Table(name="investment_balances")
public class InvestmentAccountBalance {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="account_id")
	private Account account;

	@Column(name="upload_id", nullable=false)
	private Integer uploadId;
	
	@Column(name="avail_cash")
	private BigDecimal availableCash;
	
	@Column(name="margin_balance")
	private BigDecimal marginBalance;
	
	@Column(name="short_balance")
	private BigDecimal shortBalance;
	
	@Column(name="buy_power")
	private BigDecimal buyingPower;
		
	@Column
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime date;
	
	@Column(name="created_at")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime createdAt;
	
	public InvestmentAccountBalance() {
	}
	
	public int getId() {
		return id;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Integer getUploadId() {
		return uploadId;
	}

	public Money getAvailableCash() {
		if (availableCash != null) {
			return new Money(availableCash, account.getCurrency());
		} else {
			return null;
		}
	}

	public Money getMarginBalance() {
		if (marginBalance != null) {
			return new Money(marginBalance, account.getCurrency());
		} else {
			return null;
		}
	}

	public Money getShortBalance() {
		if (shortBalance != null) {
			return new Money(shortBalance, account.getCurrency());
		} else {
			return null;
		}
	}

	public Money getBuyingPower() {
		if (buyingPower != null) {
			return new Money(buyingPower, account.getCurrency());
		} else {
			return null;
		}
	}

	public DateTime getDate() {
		return date;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}
}
