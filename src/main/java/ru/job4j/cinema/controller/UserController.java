package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@ThreadSafe
public class UserController {

    private final UserService userService;
    private static final String VALUE = "Заполните поле";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", this.userService.findAll());
        return "users";
    }

    @GetMapping("/registration")
    public String adduser(Model model, HttpSession session) {
        model.addAttribute("user", new User(0, VALUE, VALUE, VALUE, VALUE));
        sessions(model, session);
        return "registration";
    }

    @GetMapping("/registrationPage")
    public String registrationPage(Model model, HttpSession session, @RequestParam(name = "fail", required = false) Boolean fail) {
        model.addAttribute("fail", fail != null);
        sessions(model, session);
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(Model model, @ModelAttribute User user) {
        Optional<User> reg = this.userService.createUser(user);
        if (reg.isEmpty()) {
            model.addAttribute("massage", "Пользователь с такими данными уже существует");
            return "redirect:/registrationPage?fail=true";
        }
        return "success";
    }

    @GetMapping("/loginPage")
    public String loginPage(Model model, HttpSession session, @RequestParam(name = "fail", required = false) Boolean fail) {
        model.addAttribute("fail", fail != null);
        sessions(model, session);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpServletRequest req) {
        Optional<User> userDb = this.userService.findUserByEmailAndPw(
                user.getEmail(), user.getPassword()
        );
        if (userDb.isEmpty()) {
            return "redirect:/loginPage?fail=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", userDb.get());
        return "redirect:/sessions";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/sessions";
    }

    private void sessions(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Guest");
        }
        model.addAttribute("user", user);
    }
}
