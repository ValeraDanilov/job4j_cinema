package ru.job4j.cinema.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class BdUser {

    private static final Logger LOGGER = LoggerFactory.getLogger(BdUser.class.getName());

    private final BasicDataSource pool;

    public BdUser(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> createUser(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO users(username, email, phone, password) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getNumberPhone());
            ps.setString(4, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
            return Optional.of(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from users")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    String email = it.getString("email");
                    String phone = it.getString("phone");
                    String passwords = it.getString("password");
                    users.add(new User(id, name, email, phone, passwords));
                }
            }
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
        }
        users.sort(Comparator.comparing(User::getId));
        return users;
    }

    public Optional<User> findUserByEmailAndPhone(String email, String password) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users WHERE email = ? and password = ?")
        ) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("username");
                    String emails = it.getString("email");
                    String phone = it.getString("phone");
                    String passwords = it.getString("password");
                    User user = new User(id, name, emails, phone, passwords);
                    return Optional.of(user);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
