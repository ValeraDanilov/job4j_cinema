package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@ThreadSafe
public class SessionController {

    private final SessionService ses;
    private final TicketService store;
    private static final String PATH = "redirect:/sessions";
    private final AtomicInteger idUser = new AtomicInteger();
    private final AtomicInteger idSess = new AtomicInteger();

    public SessionController(SessionService ses, TicketService store) {
        this.ses = ses;
        this.store = store;
    }

    @GetMapping("/sessions")
    public String sess(Model model, HttpSession session) {
        List<Session> res = this.ses.findAll();
        model.addAttribute("sess", res);
        sessions(model, session);
        return "sessions";
    }

    @GetMapping("/sessions/{id}")
    public String session(Model model, @PathVariable Integer id, HttpSession session) {
        this.idSess.set(id);
        Optional<Session> res = this.ses.findById(id);
        if (res.isEmpty()) {
            return PATH;
        }
        model.addAttribute("sess", res.get());
        sessions(model, session);
        return "sessionName";
    }

    @GetMapping("/formRegistration/{sessId}")
    public String formDelete(Model model, HttpSession session, @PathVariable("sessId") int id) {
        Optional<Session> res = this.ses.findById(id);
        if (res.isEmpty()) {
            return PATH;
        }
        model.addAttribute("session", res);
        sessions(model, session);
        return "registration";
    }

    @GetMapping("/formAddSessions")
    public String addSessions(Model model, HttpSession session) {
        model.addAttribute("sessions",
                new Session(0, "Заполните поле", "Заполните поле", null, "Заполните поле"));
        sessions(model, session);
        return "addSessions";
    }

    @PostMapping("/createSession")
    public String createSessions(@ModelAttribute Session sessions) {
        sessions.setCreate(LocalDateTime.parse(sessions.getData(),
                DateTimeFormatter.ofPattern("yyyy MM dd HH mm")));
        Optional<Session> sess = this.ses.create(sessions);
        if (sess.isEmpty()) {
            return "redirect:/formAddSessions";
        }
        return PATH;
    }

    @PostMapping("/updateSessions")
    public String updateSession(@ModelAttribute Session updateSession) {
        boolean res = this.ses.update(updateSession);
        if (!res) {
            return String.format("redirect:/formUpdateSessions/%s", updateSession.getId());
        }
        return PATH;
    }

    @GetMapping("/formUpdateSessions/{sessionId}")
    public String formUpdateSessions(Model model, @PathVariable("sessionId") int id) {
        model.addAttribute("sess", this.ses.findById(id));
        return "updateSessions";
    }

    @GetMapping("/formDeleteSessions/{sessionId}")
    public String formDeleteSessions(Model model, @PathVariable("sessionId") int id) {
        model.addAttribute("sess", this.ses.findById(id));
        return "deleteSessions";
    }

    @PostMapping("/deleteSessions")
    public String deleteSessions(@ModelAttribute Session sessions, @RequestParam("delete") boolean delete) {
        if (delete) {
            boolean res = this.ses.delete(sessions);
            if (!res) {
                return String.format("redirect:/formDeleteSessions/%s", sessions.getId());
            }
        }
        return PATH;
    }

    @GetMapping("/ticket/{sessId}")
    public String ticket(Model model, HttpSession session, @PathVariable("sessId") int id) {
        Ticket[] tickets = this.store.findSess(id, this.idUser.get());
        for (Ticket ticket : tickets) {
            if (ticket != null && !(ticket.isCondition())) {
                int firstMin = LocalDateTime.now().getMinute();
                int secondMin = ticket.getDate().getMinute();
                int min = firstMin >= secondMin ? firstMin - secondMin : (firstMin + 60) - secondMin;
                if (min > 5) {
                    boolean res = this.store.delete(ticket);
                    if (!res) {
                        return String.format("redirect:/ticket/%s", ticket.getId());
                    }
                }
            }
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("ticketValueFalse", Arrays.stream(tickets).filter(Objects::nonNull).filter(val -> !val.isCondition()).toList());
        sessions(model, session);
        return "ticket";
    }

    @GetMapping("/formChooseTicket/{id}")
    public String session(@PathVariable("id") int id) {
        Optional<Ticket> tick = this.store.findById(id, this.idSess.get());
        if (tick.isEmpty()) {
            int row = (id + 1) / 12;
            int cell = (id + 1) % 12;
            if (cell == 0) {
                cell = 12;
            } else {
                row++;
            }
            tick = Optional.of(new Ticket(id,
                    this.idSess.get(),
                    row, cell, this.idUser.get(),
                    LocalDateTime.now(), false));
            Optional<Ticket> res = this.store.create(tick.get());
            if (res.isEmpty()) {
                return "redirect:/ticket";
            }
        }
        return String.format("redirect:/ticket/%s", this.idSess.get());
    }

    @GetMapping("/byTickets")
    public String byTicket(Model model, HttpSession session) {
        List<Ticket> tickets =
                this.store.findByIdUserAndIdSess(this.idUser.get(), this.idSess.get());
        if (!tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                if (!ticket.isCondition()) {
                    ticket.setCondition(true);
                    this.store.update(ticket);
                }
            }
        }
        model.addAttribute("tickets", tickets);
        Optional<Session> res = this.ses.findById(this.idSess.get());
        if (res.isEmpty()) {
            return PATH;
        }
        model.addAttribute("sess", res.get());
        sessions(model, session);
        return "byTickets";
    }

    @GetMapping("/successfulPurchase")
    public String success() {
        return String.format("redirect:/ticket/%s", this.idSess.get());
    }

    private void sessions(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
        this.idUser.set(user.getId());
    }
}
