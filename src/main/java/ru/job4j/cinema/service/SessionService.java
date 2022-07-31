package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.jdbc.SessionsRepository;
import ru.job4j.cinema.model.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class SessionService {

    private final SessionsRepository bdSessions;

    public SessionService(SessionsRepository bdSessions) {
        this.bdSessions = bdSessions;
    }

    public List<Session> findAll() {
        return this.bdSessions.findAll();
    }

    public Optional<Session> create(Session sessions) {
        return this.bdSessions.create(sessions);
    }

    public Optional<Session> findById(int id) {
        return this.bdSessions.findById(id);
    }

    public boolean update(Session sessions) {
       return this.bdSessions.update(sessions);
    }

    public boolean delete(Session sessions) {
       return this.bdSessions.delete(sessions);
    }
}
