package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.repository.UserRepository;
import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class UserService {

    private final UserRepository bdUser;

    public UserService(UserRepository bdUser) {
        this.bdUser = bdUser;
    }

    public Optional<User> createUser(User user) {
        return this.bdUser.createUser(user);
    }

    public List<User> findAll() {
        return this.bdUser.findAll();
    }

    public Optional<User> findUserByEmailAndPw(String email, String password) {
        return this.bdUser.findUserByEmailAndPhone(email, password);
    }
}
