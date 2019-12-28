/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import my.data.MessagesRepository;
import my.data.Topics;
import my.data.TopicsRepository;
import my.data.Users;
import my.data.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Kharisov Ruslan
 */
@Controller
@SessionAttributes({"topics", "topic"})
public class ForumController {

    private Logger log = LoggerFactory.getLogger(ForumController.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    TopicsRepository topicsRepository;

    @Autowired
    MessagesRepository messagesRepository;

    @ModelAttribute("topics")
    public List<Topics> getTopicsAttribute() {
        List<Topics> topics = topicsRepository.findByOrderByCreateAsc();
        return topics;
    }

    @ModelAttribute("topic")
    public Topics getTopicAttribute() {
        return context.getBean(Topics.class, "", new Date(), null);
    }

    @GetMapping("/")
    public String topics(Model model, Principal principal) {
        Users user = usersRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);
        List<Topics> topics = topicsRepository.findByOrderByCreateAsc();
        model.addAttribute("topics", topics);
        Topics topic = context.getBean(Topics.class, "", new Date(), user);
        model.addAttribute("topic", topic);
        return "forum";
    }

    @PostMapping("removetopic")
    public String removeTopic(@ModelAttribute("remove") long id) {
        Optional<Topics> topic = topicsRepository.findById(id);
        if (topic.isEmpty()) {
            return "redirect:/error";
        }
        messagesRepository.deleteByTopic(topic.get());
        topicsRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("addtopic")
    public String addTopic(@Valid @ModelAttribute("topic") Topics topic, Errors errors, Model model, Principal principal) {
        if (errors.hasErrors()) {
            return "forum";
        }
        Users user = usersRepository.findByUsername(principal.getName());
        topic.setCreate(new Date());
        topic.setUser(user);
        topicsRepository.save(topic);
        return "redirect:/";
    }

    @GetMapping("error")
    public String errorpage() {
        return "error";
    }
}
