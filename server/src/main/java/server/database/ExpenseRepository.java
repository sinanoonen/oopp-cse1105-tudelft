package server.database;

import commons.transactions.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Expenses.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long> {}
