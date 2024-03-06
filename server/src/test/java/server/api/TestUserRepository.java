package server.api;

import commons.User;
import commons.UserKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.UserRepository;

/**
 * A test user repository.
 */
public class TestUserRepository implements UserRepository {


    public final List<User> users = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    @Override
    public List<User> getUserByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).toList();
    }

    @Override
    public Integer updateUser(String name, String iban, String bic, String email) {
        var res = getUserByEmail(email);
        if (res.isEmpty()) {
            return 0;
        }
        getUserByEmail(email).forEach(u -> {
            if (!isNullOrEmpty(name)) {
                u.setName(name);
            }
            if (!isNullOrEmpty(iban)) {
                u.setIban(iban);
            }
            if (!isNullOrEmpty(bic)) {
                u.setBic(bic);
            }
        });
        return 1;
    }

    @Override
    public Integer deleteUserByEmail(String email) {
        if (isNullOrEmpty(email)) {
            return 0;
        }
        List<User> res = getUserByEmail(email);
        if (res.isEmpty()) {
            return 0;
        }
        User user = getUserByEmail(email).getFirst();
        return users.remove(user) ? 1 : 0;
    }

    @Override
    public <S extends User> S save(S entity) {
        users.add(entity);
        return entity;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<User> findById(UserKey userKey) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UserKey userKey) {
        return false;
    }

    @Override
    public List<User> findAll() {
        calledMethods.add("findAll");
        return users;
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<User> findAllById(Iterable<UserKey> userKeys) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public void deleteById(UserKey userKey) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UserKey> userKeys) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }


    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UserKey> userKeys) {

    }

    @Override
    public User getOne(UserKey userKey) {
        return null;
    }

    @Override
    public User getById(UserKey userKey) {
        return null;
    }

    @Override
    public User getReferenceById(UserKey userKey) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public void call(String name) {
        calledMethods.add(name);
    }
}
