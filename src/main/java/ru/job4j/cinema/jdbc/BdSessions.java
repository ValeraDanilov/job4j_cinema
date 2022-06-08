package ru.job4j.cinema.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Sessions;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class BdSessions {

    private static final Logger LOGGER = LoggerFactory.getLogger(BdSessions.class.getName());

    private final BasicDataSource pool;

    public BdSessions(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Sessions> create(Sessions sessions) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO sessions(name, text, created)" +
                     " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sessions.getName());
            ps.setString(2, sessions.getText());
            ps.setTimestamp(3, Timestamp.valueOf(sessions.getCreate()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    sessions.setId(id.getInt(1));
                }
            }
            return Optional.of(sessions);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<List<Sessions>> findAll() {
        List<Sessions> sessions = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from sessions")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    String text = it.getString("text");
                    LocalDateTime create = it.getTimestamp("created").toLocalDateTime();
                    sessions.add(new Sessions(id, name, text, create, null));
                }
            }
            return Optional.of(sessions);
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
        }
        sessions.sort(Comparator.comparing(Sessions::getId));
        return Optional.empty();
    }

    public Optional<Sessions> findById(int id) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM sessions WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    int idPost = it.getInt("id");
                    String name = it.getString("name");
                    String description = it.getString("text");
                    LocalDateTime created = it.getTimestamp("created").toLocalDateTime();
                    return Optional.of(new Sessions(idPost, name, description, created, null));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean update(Sessions sessions) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update sessions set name = ?, text = ? where id = ?",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sessions.getName());
            ps.setString(2, sessions.getText());
            ps.setInt(3, sessions.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean delete(Sessions sessions) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement pr = cn.prepareStatement("delete from sessions where id = ?")) {
            pr.setInt(1, sessions.getId());
            return pr.executeUpdate() > 0;
        } catch (SQLException se) {
            LOGGER.error(se.getMessage(), se);
            return false;
        }
    }
}
