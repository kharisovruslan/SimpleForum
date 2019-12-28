/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import my.data.Messages;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author Kharisov Ruslan
 */
@Controller
@SessionAttributes({"messages", "message", "topic"})
public class ForumMessagesController {

    private Logger log = LoggerFactory.getLogger(ForumMessagesController.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    TopicsRepository topicsRepository;

    @Autowired
    MessagesRepository messagesRepository;

    @ModelAttribute("messages")
    public List<Messages> getMessagesAttribute() {
        List<Messages> messages = new ArrayList<>();
        return messages;
    }

    @ModelAttribute("message")
    public Messages getMessageAttribute() {
        return context.getBean(Messages.class);
    }

    @ModelAttribute("topic")
    public Topics getTopicAttribute() {
        return context.getBean(Topics.class);
    }

    @GetMapping("topic/{id}")
    public String topic(@PathVariable("id") long id, Model model) {
        Optional<Topics> topic = topicsRepository.findById(id);
        if (topic.isEmpty()) {
            return "redirect:/";
        }
        List<Messages> messages = messagesRepository.findAllByTopicOrderByCreateAsc(topic.get());
        model.addAttribute("messages", messages);
        model.addAttribute("message", getMessageAttribute());
        model.addAttribute("topic", topic.get());
        return "topic";
    }

    @PostMapping("removemessage")
    public String removeMessage(@ModelAttribute("remove") long id, @ModelAttribute("topic") Topics topicCurrent) {
        Optional<Messages> message = messagesRepository.findById(id);
        if (message.isEmpty()) {
            return "/error";
        }
        messagesRepository.delete(message.get());
        return "redirect:/topic/" + topicCurrent.getId();
    }

    @PostMapping("addmessage")
    public String addMessage(@Valid @ModelAttribute("message") Messages message, Errors errors, Model model, Principal principal, @ModelAttribute("topic") Topics topic) {
        if (errors.hasErrors()) {
            return "topic";
        }
        Users user = usersRepository.findByUsername(principal.getName());
        message.setCreate(new Date());
        message.setUser(user);
        message.setTopic(topic);
        messagesRepository.save(message);
        return "redirect:/topic/" + topic.getId();
    }
}
