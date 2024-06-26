package com.expensetracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensetracker.DTO.ExpenseDTO;
import com.expensetracker.DTO.FilterDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.repository.ExpenseRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final EntityManager entityManager;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ClientService clientService, 
                              CategoryService categoryService, EntityManager entityManager) {
        this.expenseRepository = expenseRepository;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.entityManager = entityManager;
    }

    @Override
    public Expense findExpenseById(int id) {
        return expenseRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void save(ExpenseDTO expenseDTO) {
        System.out.println(expenseDTO);
        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDateTime(expenseDTO.getDateTime());
        expense.setDescription(expenseDTO.getDescription());
        expense.setClient(clientService.findClientById(expenseDTO.getClientId()));
        Category category = categoryService.findCategoryByName(expenseDTO.getCategory());
        expense.setCategory(category);
        expenseRepository.save(expense);
    }

    @Override
    public void update(ExpenseDTO expenseDTO) {
        Expense existingExpense = expenseRepository.findById(expenseDTO.getExpenseId()).orElse(null);
        existingExpense.setAmount(expenseDTO.getAmount());
        existingExpense.setDateTime(expenseDTO.getDateTime());
        existingExpense.setDescription(expenseDTO.getDescription());
        Category category = categoryService.findCategoryByName(expenseDTO.getCategory());
        existingExpense.setCategory(category);
        expenseRepository.save(existingExpense);
    }

    @Override
    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> findAllExpensesByClientId(int id) {
        return expenseRepository.findByClientId(id);
    }

    @Override
    public void deleteExpenseById(int id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public List<Expense> findFilterResult(FilterDTO filter, int clientId) {
        String query = "select e from Expense e where e.client.id = :clientId";

        if (!"all".equals(filter.getCategory())) {
            String category = filter.getCategory();
            int categoryId = categoryService.findCategoryByName(category).getId();
            query += String.format(" and e.category.id = %d", categoryId);
        }

        int from = filter.getFrom();
        int to = filter.getTo();
        query += String.format(" and e.amount between %d and %d", from, to);

        if (!"all".equals(filter.getYear())) {
            query += String.format(" and cast(substring(e.dateTime, 1, 4) as integer) = %s", filter.getYear());
        }

        if (!"all".equals(filter.getMonth())) {
            query += String.format(" and cast(substring(e.dateTime, 6, 2) as integer) = %s", filter.getMonth());
        }

        TypedQuery<Expense> expenseTypedQuery = entityManager.createQuery(query, Expense.class);
        expenseTypedQuery.setParameter("clientId", clientId);
        return expenseTypedQuery.getResultList();
    }
}
