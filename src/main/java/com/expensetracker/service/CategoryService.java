package com.expensetracker.service;
import com.expensetracker.entity.Category;

public interface CategoryService {
    Category findCategoryByName(String name);
    Category findCategoryById(int id);
}
