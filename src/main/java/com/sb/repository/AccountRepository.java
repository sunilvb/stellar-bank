package com.sb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sb.model.Account;


public interface AccountRepository extends JpaRepository<Account, Long> {
	 List<Account> findByEmail(String email);
}