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

import com.wesabe.api.util.money.Money;

@Entity
@Table(name="investment_positions")
public class InvestmentPosition {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id")
	private Account account;

	@Column(name="upload_id", nullable=false)
	private Integer uploadId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="investment_security_id")
	private InvestmentSecurity investmentSecurity;

	@Column(name="units", nullable=false)
	private BigDecimal units;

	@Column(name="unit_price", nullable=false)
	private BigDecimal unitPrice;

	@Column(name="market_value", nullable=false)
	private BigDecimal marketValue;

	@Column(name="price_date")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime priceDate;
	
	public InvestmentPosition() {
	}
	
	public Integer getId() {
		return id;
	}
	
	public Integer getUploadId() {
		return uploadId;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}

	public void setUploadId(Integer id) {
		this.uploadId = id;
	}
	
	public InvestmentSecurity getInvestmentSecurity() {
		return investmentSecurity;
	}

	public BigDecimal getUnits() {
		return units;
	}

	public Money getUnitPrice() {
		return new Money(unitPrice, account.getCurrency());
	}

	public Money getMarketValue() {
		return new Money(marketValue, account.getCurrency());
	}
	
	public DateTime getPriceDate() {
		return priceDate;
	}
}