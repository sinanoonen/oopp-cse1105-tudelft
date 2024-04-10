package server.api;


import commons.transactions.Expense;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ExpenseRepository;

/**
 * Expense Repository class for testing.
 */
public class TestExpenseRepository implements ExpenseRepository {

    public final List<Expense> expenses = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Expense> find(long code) {
        return expenses.stream()
                .filter(e -> e.getId() == code)
                .findFirst();
    }

    @Override
    public List<Expense> findAll() {
        calledMethods.add("findAll");
        return expenses;
    }

    @Override
    public <S extends Expense> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Expense> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Expense> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expense getById(Long code) {
        call("getByID");
        return find(code).get();
    }

    @Override
    public Expense getReferenceById(Long code) {
        call("getReferenceByLong");
        return find(code).get();
    }

    @Override
    public <S extends Expense> S save(S entity) {
        call("save");
        expenses.add(entity);
        return entity;
    }

    @Override
    public long count() {
        return expenses.size();
    }

    @Override
    public <S extends Expense> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Expense> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Expense> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub
    }

    @Override
    public Expense getOne(Long along) {
        return null;
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Expense> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Expense> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Expense> findById(Long id) {
        call("findById");
        for (Expense expense : expenses) {
            if (expense.getId() == id) {
                return Optional.of(expense);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
    }

    @Override
    public void delete(Expense entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll(Iterable<? extends Expense> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Expense> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Expense, R> R findBy(Example<S> example,
                                           Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}
