/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.controller;

import java.security.Principal;
import java.util.Optional;
import javax.validation.Valid;
import my.data.UserAuthorities;
import my.data.UserAuthoritiesRepository;
import my.data.Users;
import my.data.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Kharisov Ruslan
 */
@Controller
public class UsersController {

    private Logger log = LoggerFactory.getLogger(UsersController.class);
    @Autowired
    private ApplicationContext context;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UserAuthoritiesRepository userAuthoritiesRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("profile")
    public String profile(Model model, Principal principal) {
        Users user = usersRepository.findByUsername(principal.getName());
        if (user == null) {
            return "/error";
        }
        model.addAttribute("user", user);
        model.addAttribute("readonly", false);
        return "profile";
    }

    @GetMapping("userprofile/{id}")
    public String userprofile(@PathVariable("id") long id, Model model) {
        Optional<Users> user = usersRepository.findById(id);
        if (user.isEmpty()) {
            return "/error";
        }
        model.addAttribute("user", user);
        model.addAttribute("readonly", true);
        return "profile";
    }

    @PostMapping("profile")
    public String profileSave(@Valid @ModelAttribute("user") Users user, Errors errors, Model model, Principal principal) {
        if (errors.hasErrors()) {
            model.addAttribute("registerform", user);
            return "register";
        }
        Users userCurrent = usersRepository.findByUsername(principal.getName());
        userCurrent.setEmail(user.getEmail());
        userCurrent.setFirstname(user.getFirstname());
        userCurrent.setSecondname(user.getSecondname());
        userCurrent.setSurname(user.getSurname());
        userCurrent.setPassword(encoder.encode(user.getPassword()));
        usersRepository.save(userCurrent);
        return "redirect:/";
    }

    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("registerform", context.getBean(Users.class));
        return "register";
    }

    @PostMapping("register")
    public String registerNewUser(@Valid @ModelAttribute("registerform") Users user, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("registerform", user);
            return "register";
        }
        if (usersRepository.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "login_not_available", "this login not available");
            model.addAttribute("registerform", user);
            return "register";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(true);
        usersRepository.save(user);
        if (usersRepository.count() == 1) {
            userAuthoritiesRepository.save(context.getBean(UserAuthorities.class, user, "ROLE_ADMIN"));
        } else {
            userAuthoritiesRepository.save(context.getBean(UserAuthorities.class, user, "ROLE_USER"));
        }
        return "redirect:/";
    }
}
