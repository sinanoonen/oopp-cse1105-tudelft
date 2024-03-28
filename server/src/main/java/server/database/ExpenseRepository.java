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

    @Modifying
    @Transactional
    @Query(value = "UPDATE expenses e JOIN transactions t ON e.id = t.id SET e.description = ?1, "
            + "t.owner = ?2, t.date = ?3, t.amount = ?4 WHERE e.id = ?5", nativeQuery = true)
    Integer updateExpense(String description, String owner, LocalDate date, float amount, Long id);
}

