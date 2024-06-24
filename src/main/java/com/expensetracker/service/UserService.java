package com.expensetracker.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.expensetracker.DTO.WebUser;
import com.expensetracker.entity.User;

public interface UserService extends UserDetailsService {
    User findUserByUserName(String username);

    void save(WebUser webUser);
}