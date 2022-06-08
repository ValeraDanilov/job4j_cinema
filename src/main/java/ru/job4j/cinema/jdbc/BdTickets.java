package ru.job4j.cinema.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Ticket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class BdTickets {

    private static final Logger LOGGER = LoggerFactory.getLogger(BdTickets.class.getName());

    private final BasicDataSource pool;

    public BdTickets(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Ticket> create(Ticket tickets) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(ticket_id, session_id, pos_row, cell, user_id, date, condition)" +
                             " VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tickets.getId());
            ps.setInt(2, tickets.getSessId());
            ps.setInt(3, tickets.getRow());
            ps.setInt(4, tickets.getCell());
            ps.setInt(5, tickets.getUserId());
            ps.setTimestamp(6, Timestamp.valueOf(tickets.getDate()));
            ps.setBoolean(7, tickets.isCondition());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    tickets.setId(id.getInt(1));
                }
            }
            return Optional.of(tickets);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<List<Ticket>> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from ticket")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(createTicketFromResultSe(it));
                }
            }
            tickets.sort(Comparator.comparing(Ticket::getId));
            return Optional.of(tickets);
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
        }
        return Optional.empty();
    }

    private Ticket createTicketFromResultSe(ResultSet it) throws SQLException {
        int id = it.getInt("ticket_id");
        int sessId = it.getInt("session_id");
        int posRow = it.getInt("pos_row");
        int cell = it.getInt("cell");
        int userId = it.getInt("user_id");
        LocalDateTime date = it.getTimestamp("date").toLocalDateTime();
        boolean condition = it.getBoolean("condition");
        return new Ticket(id, sessId, posRow, cell, userId, date, condition);

    }

    public Optional<Ticket> findById(int id, int idSess) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM ticket WHERE ticket_id = ? and session_id = ? and condition = true")) {
            ps.setInt(1, id);
            ps.setInt(2, idSess);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(createTicketFromResultSe(it));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<List<Ticket>> findByIdUserAndIdSess(int userId, int sessId) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket WHERE user_id = ? and session_id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, sessId);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(createTicketFromResultSe(it));
                }
            }
            tickets.sort(Comparator.comparing(Ticket::getId));
            return Optional.of(tickets);
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
        }
        return Optional.empty();
    }

    public boolean update(Ticket ticket) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update ticket set condition = ? where ticket_id  = ?",
                     Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pr = cn.prepareStatement("delete from ticket where ticket_id = ? and condition = false")) {
            ps.setBoolean(1, ticket.isCondition());
            ps.setInt(2, ticket.getId());
            ps.executeUpdate();
            pr.setInt(1, ticket.getId());
           return pr.executeUpdate() > 0;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean delete(Ticket ticket) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from ticket where ticket_id = ?")) {
            ps.setInt(1, ticket.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
            return false;
        }
    }
}
