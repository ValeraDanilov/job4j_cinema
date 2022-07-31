package ru.job4j.cinema.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.jdbc.TicketsRepository;
import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
public class TicketService {

    private final TicketsRepository ticket;

    public TicketService(TicketsRepository ticket) {
        this.ticket = ticket;
    }

    public Optional<Ticket> create(Ticket ticket) {
        return this.ticket.create(ticket);
    }

    public Ticket[] findSess(int id, int userId) {
        List<Ticket> res = this.ticket.findAll().stream().filter(value -> value.getSessId() == id && value.getUserId() == userId).toList();
        Ticket[] tickets = new Ticket[108];
        for (Ticket tick : res) {
            tickets[tick.getId()] = tick;
        }
        return tickets;
    }

    public Optional<Ticket> findById(int id, int idSess) {
        return this.ticket.findById(id, idSess);
    }

    public List<Ticket> findByIdUserAndIdSess(int userId, int sessId) {
        return this.ticket.findByIdUserAndIdSess(userId, sessId);
    }

    public boolean update(Ticket ticket) {
        return this.ticket.update(ticket);
    }

    public boolean delete(Ticket ticket) {
        return this.ticket.delete(ticket);
    }
}
