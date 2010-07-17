package com.wesabe.api.accounts.entities;

import javax.persistence.*;

@Entity
@Table(name="merchants")
public class Merchant {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@Column
	private String name;
	
	public Merchant() {
	}
	
	public Merchant(String name) {
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
