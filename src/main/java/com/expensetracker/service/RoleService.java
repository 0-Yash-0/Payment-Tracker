package com.expensetracker.service;

import com.expensetracker.entity.Role;

public interface RoleService{
    Role findRoleByName(String name);
}
