package com.homework;

public class Person {
	private String name;
	private String surname;
	private Boolean blacklisted = false;
	private String country = "lv";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public Boolean getBlacklisted() {
		return blacklisted;
	}
	public void setBlacklisted(Boolean blacklisted) {
		this.blacklisted = blacklisted;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}
