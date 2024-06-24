package com.expensetracker.service;
import com.expensetracker.entity.Client;

public interface ClientService {
    void saveClient(Client client);
    Client findClientById(int id);
}