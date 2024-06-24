package com.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {

}