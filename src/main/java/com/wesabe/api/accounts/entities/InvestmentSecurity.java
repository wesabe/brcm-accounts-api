package com.wesabe.api.accounts.entities;

import javax.persistence.*;

@Entity
@Table(name="investment_securities")
public class InvestmentSecurity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column
	private String name;
	
	@Column
	private String ticker;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@JoinColumn(name="ticker", referencedColumnName="symbol", nullable=true, insertable=false, updatable=false)
	private Stock stock;
	
	public InvestmentSecurity() {
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		if (this.stock != null) {
			return this.stock.getName();
		} else {
			return null;
		}
	}
	
	public String getTicker() {
		return this.ticker;
	}
}
