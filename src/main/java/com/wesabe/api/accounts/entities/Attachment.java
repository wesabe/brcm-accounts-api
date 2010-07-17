package com.wesabe.api.accounts.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="attachments")
public class Attachment {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="account_key")
	private String accountKey;
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="guid")
	private String guid;
	
	@Column(name="description")
	private String description;
	
	@Column(name="content_type")
	private String contentType;
	
	@Column(name="size")
	private Integer size;
	
	public Integer getId() {
		return id;
	}
	
	public String getAccountKey() {
		return accountKey;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Integer getSize() {
		return size;
	}
}
