package com.wesabe.api.accounts.entities;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.BatchSize;

import com.google.common.collect.Sets;
import com.wesabe.api.util.money.Money;

@Entity
@DiscriminatorValue("InvestmentAccount")
@NamedQueries({
	@NamedQuery(
	  name="com.wesabe.api.accounts.InvestmentAccount.findAllByAccountKey",
	  query="SELECT a FROM InvestmentAccount a" +
	  		" WHERE a.accountKey = :accountKey AND a.status IN (:statuses)" +
	  		" ORDER BY a.relativeId ASC"
	)
})
public class InvestmentAccount extends Account {
	@OneToMany(mappedBy="account")
	@BatchSize(size=100)
	@OrderBy("priceDate DESC, uploadId DESC")
	private Set<InvestmentPosition> investmentPositions = Sets.newHashSet();

	@OneToMany(mappedBy="account")
	@BatchSize(size=10)
	@OrderBy("date DESC, createdAt DESC")
	private Set<InvestmentAccountBalance> investmentAccountBalances = Sets.newHashSet();

	public InvestmentAccount() {
	}
	
	// investment account "balance" is sum of market value of positions plus available cash
	@Override
	public Money getBalance() {
		Money availableCash = getAvailableCash();
		if (availableCash != null) {
			return getMarketValue().add(availableCash);			
		} else {
			return getMarketValue();
		}
	}

	// market value is the sum of the market value of all current positions
	public Money getMarketValue() {
		Money marketValue = Money.zero(getCurrency());
		for (InvestmentPosition position : getCurrentInvestmentPositions()) {
			marketValue = marketValue.add(position.getMarketValue());
		}
		return marketValue;
	}
	
	public Set<InvestmentPosition> getInvestmentPositions() {
		return investmentPositions;
	}
	
	public Set<InvestmentPosition> getCurrentInvestmentPositions() {
		// assumes the list is already sorted by priceDate, uploadId
		Set<InvestmentPosition> currentPositions = Sets.newHashSet();
		Set<InvestmentPosition> allPositions = getInvestmentPositions();
		if (allPositions.size() > 0) {
			// get the first position
			Iterator<InvestmentPosition> iterator = allPositions.iterator();
			InvestmentPosition firstPosition = iterator.next();
			currentPositions.add(firstPosition);
			Integer firstUploadId = firstPosition.getUploadId();
			// get all positions with that same upload id as the first position
			while (iterator.hasNext()) {
				InvestmentPosition position = iterator.next();
				if (position.getUploadId().equals(firstUploadId)) {
					currentPositions.add(position);
				} else {
					break;
				}
			}
		}
		return currentPositions;
	}

	public Set<InvestmentAccountBalance> getInvestmentAccountBalances() {
		return investmentAccountBalances;
	}
	
	public InvestmentAccountBalance getCurrentInvestmentAccountBalance() {
		Set<InvestmentAccountBalance> balances = getInvestmentAccountBalances();
		if (balances.size() > 0) {
			return balances.iterator().next();
		} else {
			return null;
		}
	}
	
	public Money getAvailableCash() {
		InvestmentAccountBalance balance = getCurrentInvestmentAccountBalance();
		if (balance != null) {
			return balance.getAvailableCash();
		} else {
			return null;
		}
	}
}
