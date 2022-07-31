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
public class TicketsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketsRepository.class.getName());

    private final BasicDataSource pool;

    public TicketsRepository(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Ticket> create(Ticket tickets) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(sessid, row, cell, userid, date, condition)" +
                             " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tickets.getSessId());
            ps.setInt(2, tickets.getRow());
            ps.setInt(3, tickets.getCell());
            ps.setInt(4, tickets.getUserId());
            ps.setTimestamp(5, Timestamp.valueOf(tickets.getDate()));
            ps.setBoolean(6, tickets.isCondition());
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

    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from ticket t order by t.id")) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(createTicketFromResultSe(it));
                }
            }
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
        }
        return tickets;
    }

    private Ticket createTicketFromResultSe(ResultSet it) throws SQLException {
        int id = it.getInt("id");
        int sessId = it.getInt("sessId");
        int row = it.getInt("row");
        int cell = it.getInt("cell");
        int userId = it.getInt("userId");
        LocalDateTime date = it.getTimestamp("date").toLocalDateTime();
        boolean condition = it.getBoolean("condition");
        return new Ticket(id, sessId, row, cell, userId, date, condition);
    }

    public Optional<Ticket> findById(int row, int cell, int idSess) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM ticket WHERE row = ? and cell = ? and sessId = ? and condition = true")) {
            ps.setInt(1, row);
            ps.setInt(2, cell);
            ps.setInt(3, idSess);
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

    public List<Ticket> findByIdUserAndIdSess(int userId, int sessId) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket WHERE userId = ? and sessId = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, sessId);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(createTicketFromResultSe(it));
                }
            }
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
        }
        return tickets;
    }

    public boolean update(Ticket ticket) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update ticket set condition = ? where id  = ?",
                     Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pr = cn.prepareStatement("delete from ticket where id = ? and condition = false")) {
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
             PreparedStatement ps = cn.prepareStatement("delete from ticket where id = ?")) {
            ps.setInt(1, ticket.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception eo) {
            LOGGER.error(eo.getMessage(), eo);
            return false;
        }
    }
}
