package com.wesabe.api.accounts.entities;

import javax.persistence.*;

@Entity
@Table(name="financial_insts")
public class FinancialInst {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column
	private String name;
	
	@Column(name="wesabe_id")
	private String wesabeId;
	
	public FinancialInst() {
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getWesabeId() {
		return wesabeId; 
	}
	
	@Override
	public String toString() {
		return getName();
	}
}