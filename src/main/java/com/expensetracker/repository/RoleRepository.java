package com.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String role);
}