package com.cinefms.dbstore.utils.mongo.entities;

import com.cinefms.dbstore.api.impl.BaseDBStoreEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserEntity extends BaseDBStoreEntity {
	private UUID uuid;
	private Date birthday;
	private String firstName;
	private String lastName;
	private String username;
	@JsonProperty("password")
	private String encryptedPassword;
	private List<Address> addresses;

	public UserEntity() {
		uuid = UUID.randomUUID();
	}

	public UUID getUuid() {
		return uuid;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	@JsonIgnore
	public String getFullName() {
		return firstName + " " + lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonIgnore
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	@JsonSetter("password")
	protected void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public void setPassword(String password) {
		this.encryptedPassword = password.hashCode() + "";
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

}
