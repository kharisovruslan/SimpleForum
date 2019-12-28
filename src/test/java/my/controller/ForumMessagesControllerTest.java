/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.controller;

import java.util.Date;
import java.util.List;
import my.data.Messages;
import my.data.MessagesRepository;
import my.data.Topics;
import my.data.TopicsRepository;
import my.data.UserAuthorities;
import my.data.UserAuthoritiesRepository;
import my.data.Users;
import my.data.UsersRepository;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kharisov Ruslan
 */
@WebMvcTest(ForumController.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UsersControllerTest.myConfig.class)
public class ForumMessagesControllerTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private UserAuthoritiesRepository userAuthoritiesRepository;

    @Autowired
    TopicsRepository topicsRepository;

    @Autowired
    MessagesRepository messagesRepository;

    @Autowired
    ForumMessagesController forumMessagesController;

    private final String messagetextForRemove = "my message test fot remove";
    private final String messagetextForNew = "my message test fot new";
    private final String messagetext = "my message test";
    private final String topictext = "test topic";

    public ForumMessagesControllerTest() {
    }

    @Before
    public void setUp() {
        if (usersRepository.count() == 0) {
            Users user = new Users("user", "surname", "firstname", "secondname", "user@mail.org", encoder.encode("12345"), true);
            Users admin = new Users("admin", "surname", "firstname", "secondname", "admin@mail.org", encoder.encode("12345"), true);
            usersRepository.save(user);
            usersRepository.save(admin);
            userAuthoritiesRepository.save(new UserAuthorities(user, "ROLE_USER"));
            userAuthoritiesRepository.save(new UserAuthorities(admin, "ROLE_ADMIN"));
            Topics topic = new Topics(topictext, new Date(), user);
            topicsRepository.save(topic);
            messagesRepository.save(new Messages(messagetext, topic, new Date(), user));
            messagesRepository.save(new Messages(messagetextForRemove, topic, new Date(), user));
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMessagesAttribute method, of class ForumMessagesController.
     */
    @Test
    public void testGetMessagesAttribute() {
        System.out.println("getMessagesAttribute");
        List<Messages> message = forumMessagesController.getMessagesAttribute();
        assertTrue(message.size() == 0);
    }

    /**
     * Test of getMessageAttribute method, of class ForumMessagesController.
     */
    @Test
    public void testGetMessageAttribute() {
        System.out.println("getMessageAttribute");
        Messages message = forumMessagesController.getMessageAttribute();
        assertTrue(message instanceof Messages);
    }

    /**
     * Test of getTopicAttribute method, of class ForumMessagesController.
     */
    @Test
    public void testGetTopicAttribute() {
        System.out.println("getTopicAttribute");
        Topics topic = forumMessagesController.getTopicAttribute();
        assertTrue(topic instanceof Topics);
    }

    /**
     * Test of topic method, of class ForumMessagesController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testTopic() throws Exception {
        System.out.println("topic");
        Topics topic = topicsRepository.findAllByTitle(topictext);
        mvc.perform(MockMvcRequestBuilders.get("/topic/" + Long.toString(topic.getId())).accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("my message test")));
    }

    /**
     * Test of removeMessage method, of class ForumMessagesController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testRemoveMessage() throws Exception {
        System.out.println("removeMessage");
        Topics topic = topicsRepository.findAllByTitle(topictext);
        Messages messageforremove = messagesRepository.findByTopicAndText(topic, messagetextForRemove);
        String topicAddress = "/topic/" + Long.toString(topic.getId());
        mvc.perform(MockMvcRequestBuilders.get(topicAddress).accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString(messagetextForRemove)));
        mvc.perform(MockMvcRequestBuilders.post("/removemessage").param("remove", Long.toString(messageforremove.getId())).flashAttr("topic", topic)
                .accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl(topicAddress))
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.not(CoreMatchers.containsString(messagetextForRemove))));
    }

    /**
     * Test of addMessage method, of class ForumMessagesController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testAddMessage() throws Exception {
        System.out.println("addMessage");
        Topics topic = topicsRepository.findAllByTitle(topictext);
        String topicAddress = "/topic/" + Long.toString(topic.getId());
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", messagetextForNew);
        mvc.perform(MockMvcRequestBuilders.post("/addmessage").params(params).flashAttr("topic", topic)
                .accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl(topicAddress))
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.not(CoreMatchers.containsString(messagetextForNew))));
    }

}
