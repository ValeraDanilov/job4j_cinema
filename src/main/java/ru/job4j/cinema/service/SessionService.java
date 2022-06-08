package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.jdbc.BdSessions;
import ru.job4j.cinema.model.Sessions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class SessionService {

    private final BdSessions bdSessions;

    public SessionService(BdSessions bdSessions) {
        this.bdSessions = bdSessions;
    }

    public Optional<List<Sessions>> findAll() {
        return this.bdSessions.findAll();
    }

    public Optional<Sessions> create(Sessions sessions) throws SQLException {
        return this.bdSessions.create(sessions);
    }

    public Optional<Sessions> findById(int id) {
        return this.bdSessions.findById(id);
    }

    public boolean update(Sessions sessions) {
       return this.bdSessions.update(sessions);
    }

    public boolean delete(Sessions sessions) {
       return this.bdSessions.delete(sessions);
    }
}
