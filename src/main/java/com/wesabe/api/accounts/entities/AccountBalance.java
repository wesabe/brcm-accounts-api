package com.wesabe.api.accounts.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.wesabe.api.util.money.CurrencyMismatchException;
import com.wesabe.api.util.money.Money;

@Entity
@Table(name="account_balances")
public class AccountBalance {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column(nullable=false, name="balance")
	private BigDecimal balance;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="account_id")
	private Account account;
	
	@Column(name="balance_date")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime date;
	
	@Column(name="created_at")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime createdAt;
	
	public AccountBalance() {
		// nothing here yet
	}
	
	public AccountBalance(Account account, BigDecimal balance, DateTime date) {
		this.account = account;
		this.balance = balance;
		this.date = date;
	}
	
	public AccountBalance(Account account, Money balance, DateTime date) {
		this(account, balance.getValue(), date);
		
		if (!account.getCurrency().equals(balance.getCurrency())) {
			throw new CurrencyMismatchException("create an AccountBalance with", account.getCurrency(), balance.getCurrency());
		}
	}
	
	public int getId() {
		return id;
	}

	public Money getBalance() {
		return new Money(balance, account.getCurrency());
	}
	
	public Account getAccount() {
		return account;
	}
	
	public DateTime getDate() {
		return date;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}
}
