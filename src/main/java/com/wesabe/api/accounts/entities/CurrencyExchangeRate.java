package com.wesabe.api.accounts.entities;

import java.util.Currency;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.wesabe.api.util.money.CurrencyCodeParser;
import com.wesabe.api.util.money.UnknownCurrencyCodeException;

@Entity
@Table(name="currency_exchange_rates")
@NamedQueries({
	@NamedQuery(
		name="com.wesabe.api.accounts.entities.CurrencyExchangeRate.findRecent",
		query="SELECT r FROM CurrencyExchangeRate r" +
			  " WHERE date > :date"
	),
	@NamedQuery(
		name="com.wesabe.api.accounts.entities.CurrencyExchangeRate.findAll",
		query="SELECT r FROM CurrencyExchangeRate r"
	)
})
public class CurrencyExchangeRate {
	private static final CurrencyCodeParser CURRENCY_CODE_PARSER = new CurrencyCodeParser();

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="currency")
	private String currencyCode;
	
	@Column(name="date")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime date;
	
	// the number of units in this currency which are equal to one dollar USD
	@Column(name="rate")
	private Double rate;
	
	public CurrencyExchangeRate() {
	}
	
	public CurrencyExchangeRate(Currency currency, Double rate, DateTime date) {
		this.date = date;
		this.currencyCode = currency.getCurrencyCode();
		this.rate = rate;
	}
	
	public Integer getId() {
		return id;
	}
	
	public DateTime getDate() {
		return date;
	}
	
	public Double getRate() {
		return rate;
	}
	
	public Currency getCurrency() throws UnknownCurrencyCodeException {
		return CURRENCY_CODE_PARSER.parse(currencyCode);
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("{").append("USD:").append(currencyCode).append("=").append(rate).append("@").append(date.toLocalDate());
		return output.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(date, currencyCode, rate);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CurrencyExchangeRate) {
			CurrencyExchangeRate that = (CurrencyExchangeRate) obj;
			return Objects.equal(date, that.date) &&
				   Objects.equal(rate, that.rate) &&
				   Objects.equal(currencyCode, that.currencyCode);
		}
		return false;
	}
}
