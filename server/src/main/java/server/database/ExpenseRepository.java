package server.database;

import commons.User;
import commons.transactions.Expense;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


/**
 * Repository for Expenses.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}

