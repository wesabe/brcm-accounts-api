package com.wesabe.api.accounts.entities;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="stocks")
public class Stock implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column
	private String name;
	
	@Column
	private String symbol;
	
	@Column
	private String exchange;

	public Stock() {}
	
	public Integer getId() {
		return id;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getExchange() {
		return exchange;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
