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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@ThreadSafe
public class SessionController {

    private final SessionService ses;
    private final TicketService store;
    private static final String PATH = "redirect:/sessions";
    private final AtomicInteger idUser = new AtomicInteger();
    private final AtomicInteger idSess = new AtomicInteger();
    private final AtomicInteger count = new AtomicInteger();

    public SessionController(SessionService ses, TicketService store) {
        this.ses = ses;
        this.store = store;
    }

    @GetMapping("/sessions")
    public String getSessions(Model model, HttpSession session) {
        List<Session> res = this.ses.findAll();
        model.addAttribute("sess", res);
        sessions(model, session);
        return "sessions";
    }

    @GetMapping("/sessions/{id}")
    public String sessionName(Model model, @PathVariable Integer id, HttpSession session) {
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
    public String formRegistration(Model model, HttpSession session, @PathVariable("sessId") int id) {
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
                new Session(0, "Заполните поле", "Заполните поле", null));
        sessions(model, session);
        return "addSessions";
    }

    @RequestMapping(value = "/createSession", method = RequestMethod.POST)
    public String createSessions(@ModelAttribute("sessions") Session sessions) {
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
    public String deleteSessions(@PathVariable("sessionId") int id) {
        Optional<Session> resSess = this.ses.findById(id);
        if (resSess.isPresent()) {
            boolean res = this.ses.delete(resSess.get());
            if (!res) {
                return String.format("redirect:/formDeleteSessions/%s", resSess.get().getId());
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

        model.addAttribute("count", priceCount(tickets));
        model.addAttribute("price", priceCount(tickets) * 200);
        model.addAttribute("sess", this.ses.findById(this.idSess.get()));
        model.addAttribute("tickets", tickets);
        model.addAttribute("ticketValueFalse", Arrays.stream(tickets)
                .filter(Objects::nonNull)
                .filter(val -> !val.isCondition()).toList());
        sessions(model, session);
        return "ticket";
    }

    private Long priceCount(Ticket[] tickets) {
        return Arrays.stream(tickets)
                .filter(Objects::nonNull)
                .filter(user -> user.getUserId() == this.idUser.get())
                .filter(con -> !con.isCondition())
                .map(Ticket::getPrice).count();
    }

    @GetMapping("/formChooseTicket/{id}")
    public String chooseTicket(@PathVariable("id") int id, Model model, HttpSession session) {
        sessions(model, session);
        int row = (id + 1) / 12;
        int cell = (id + 1) % 12;
        if (cell == 0) {
            cell = 12;
        } else {
            row++;
        }
        Optional<Ticket> tick = this.store.findById(row, cell, this.idSess.get(), this.idUser.get());
        if (tick.isEmpty() || tick.get().getUserId() != this.idUser.get()) {
            this.count.incrementAndGet();
            tick = Optional.of(new Ticket(id,
                    this.idSess.get(),
                    row, cell, this.idUser.get(),
                    LocalDateTime.now(), false, 200));
            Optional<Ticket> res = this.store.create(tick.get());
            if (res.isEmpty()) {
                return "ticket";
            }
        } else if (!tick.get().isCondition() && this.idUser.get() == tick.get().getUserId()) {
            this.store.delete(tick.get());
        }
        return String.format("redirect:/ticket/%s", this.idSess.get());
    }

    @GetMapping("/successfulPurchase")
    public String byTicket(Model model, HttpSession session) {
        model.addAttribute("sess", this.idSess.get());
        sessions(model, session);
        List<Ticket> tickets =
                this.store.findByIdUserAndIdSess(this.idUser.get(), this.idSess.get());
        if (!tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                if (!ticket.isCondition()) {
                    ticket.setCondition(true);
                    this.store.update(ticket);
                }
            }
            return "successfulPurchase";
        }
        return "purchaseNotCompleted";
    }

    private void sessions(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Guest");
        }
        model.addAttribute("user", user);
        this.idUser.set(user.getId());
    }
}
