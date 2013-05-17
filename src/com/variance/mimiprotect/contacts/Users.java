package com.variance.mimiprotect.contacts;

import java.util.List;

import com.variance.vjax.android.annotations.GenericCollectionType;

public class Users {
	@GenericCollectionType(User.class)
	private List<User> users;

	public Users() {
	}

	public Users(List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
