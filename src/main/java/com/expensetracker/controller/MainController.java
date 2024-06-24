package com.expensetracker.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.expensetracker.DTO.ExpenseDTO;
import com.expensetracker.DTO.FilterDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Client;
import com.expensetracker.entity.Expense;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	ExpenseService expenseService;
	CategoryService categoryService;

	@Autowired
	public MainController(ExpenseService expenseService, CategoryService categoryService) {
		this.expenseService = expenseService;
		this.categoryService = categoryService;
	}

	@GetMapping("/")
	public String landingPage(HttpSession session, Model model) {
		Client client = (Client) session.getAttribute("client");
		model.addAttribute("sessionClient", client);
		return "landing-page";
	}

	@GetMapping("/showAdd")
	public String addExpense(Model model) {
		model.addAttribute("expense", new ExpenseDTO());
		return "add-expense";
	}

	@PostMapping("/submitAdd")
	public String submitAdd(@ModelAttribute("expense") ExpenseDTO expenseDTO, HttpSession session) {
		Client client = (Client) session.getAttribute("client");
		expenseDTO.setClientId(client.getId());
		expenseService.save(expenseDTO);
		return "redirect:/list";
	}

	@GetMapping("/list")
	public String list(Model model, HttpSession session) {
		Client client = (Client) session.getAttribute("client");
		if (client == null) {
			// Redirect to login page if client is not authenticated
			return "redirect:/login";
		}

		int clientId = client.getId();
		List<Expense> expenseList = expenseService.findAllExpensesByClientId(clientId);
		for (Expense expense : expenseList) {
			if (expense.getCategory() != null) {
				Category category = categoryService.findCategoryById(expense.getCategory().getId());
				if (category != null) {
					expense.setCategoryName(category.getName());
				} else {
					// Handle scenario where category is null (optional)
					expense.setCategoryName("Unknown Category");
				}
			} else {
				// Handle scenario where expense category is null (optional)
				expense.setCategoryName("Unknown Category");
			}

			LocalDateTime dateTime = LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			expense.setDate(dateTime.toLocalDate().toString());
			expense.setTime(dateTime.toLocalTime().toString());
		}

		model.addAttribute("expenseList", expenseList);
		model.addAttribute("filter", new FilterDTO());
		return "list-page";
	}

	@GetMapping("/showUpdate")
	public String showUpdate(@RequestParam("expId") int id, Model model) {
		Expense expense = expenseService.findExpenseById(id);
		if (expense == null) {
			// Handle scenario where expense with given id is not found
			return "error-page"; // Redirect to error page or handle gracefully
		}

		ExpenseDTO expenseDTO = new ExpenseDTO();
		expenseDTO.setAmount(expense.getAmount());
		if (expense.getCategory() != null) {
			Category category = categoryService.findCategoryById(expense.getCategory().getId());
			if (category != null) {
				expenseDTO.setCategory(category.getName());
			} else {
				// Handle scenario where category is null (optional)
				expenseDTO.setCategory("Unknown Category");
			}
		} else {
			// Handle scenario where expense category is null (optional)
			expenseDTO.setCategory("Unknown Category");
		}
		expenseDTO.setDescription(expense.getDescription());
		expenseDTO.setDateTime(expense.getDateTime());

		model.addAttribute("expense", expenseDTO);
		model.addAttribute("expenseId", id);
		return "update-page";
	}

	@PostMapping("/submitUpdate")
	public String update(@RequestParam("expId") int id, @ModelAttribute("expense") ExpenseDTO expenseDTO,
			HttpSession session) {
		Client client = (Client) session.getAttribute("client");
		expenseDTO.setExpenseId(id);
		expenseDTO.setClientId(client.getId());
		expenseService.update(expenseDTO);
		return "redirect:/list";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("expId") int id) {
		expenseService.deleteExpenseById(id);
		return "redirect:/list";
	}

	@PostMapping("/processFilter")
	public String processFilter(@ModelAttribute("filter") FilterDTO filter, Model model) {
		List<Expense> expenseList = expenseService.findFilterResult(filter);
		for (Expense expense : expenseList) {
			if (expense.getCategory() != null) {
				Category category = categoryService.findCategoryById(expense.getCategory().getId());
				if (category != null) {
					expense.setCategoryName(category.getName());
				} else {
					// Handle scenario where category is null (optional)
					expense.setCategoryName("Unknown Category");
				}
			} else {
				// Handle scenario where expense category is null (optional)
				expense.setCategoryName("Unknown Category");
			}

			LocalDateTime dateTime = LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			expense.setDate(dateTime.toLocalDate().toString());
			expense.setTime(dateTime.toLocalTime().toString());
		}

		model.addAttribute("expenseList", expenseList);
		return "filter-result";
	}

}