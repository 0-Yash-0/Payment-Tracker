package com.expensetracker.service;
import java.util.List;

import com.expensetracker.DTO.ExpenseDTO;
import com.expensetracker.DTO.FilterDTO;
import com.expensetracker.entity.Expense;

public interface ExpenseService {
    Expense findExpenseById(int id);
    void save(ExpenseDTO expenseDTO);
    void update(ExpenseDTO expenseDTO);
    List<Expense> findAllExpenses();
    List<Expense> findAllExpensesByClientId(int id);
    void deleteExpenseById(int id);
    List<Expense> findFilterResult(FilterDTO filter);

}