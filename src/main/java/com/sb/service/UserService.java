package com.sb.service;

import com.sb.model.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
}
