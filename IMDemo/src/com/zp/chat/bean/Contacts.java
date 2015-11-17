package com.zp.chat.bean;

public class Contacts {
	
	private String name ;
	private String user ;
	private int unread ;
	
	public Contacts(String name, String user) {
		super();
		this.name = name;
		this.user = user;
		this.unread = 0;
	}
	
	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	
}
