/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.controller;

import java.util.Date;
import java.util.List;
import my.data.Topics;
import my.data.TopicsRepository;
import my.data.UserAuthorities;
import my.data.UserAuthoritiesRepository;
import my.data.Users;
import my.data.UsersRepository;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import static org.junit.Assert.*;
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
public class ForumControllerTest {

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
    ForumController forumController;

    public ForumControllerTest() {
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
            topicsRepository.save(new Topics("test topic", new Date(), user));
            Topics removeTopic = new Topics("test topic for remove", new Date(), user);
            topicsRepository.save(removeTopic);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getTopicsAttribute method, of class ForumController.
     */
    @Test
    public void testGetTopicsAttribute() {
        System.out.println("getTopicsAttribute");
        List<Topics> topic = forumController.getTopicsAttribute();
        assertTrue(topic.size() == 1);
    }

    /**
     * Test of getTopicAttribute method, of class ForumController.
     */
    @Test
    public void testGetTopicAttribute() {
        System.out.println("getTopicAttribute");
        Topics topic = forumController.getTopicAttribute();
        assertTrue(topic instanceof Topics);
    }

    /**
     * Test of topics method, of class ForumController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testTopics() throws Exception {
        System.out.println("testTopics");
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("test topic")));
    }

    /**
     * Test of removeTopic method, of class ForumController.
     */
    @WithMockUser(username = "admin")
    @Test
    public void testRemoveTopic() throws Exception {
        System.out.println("removeTopic");
        String titleTopic = "test topic for remove";
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString(titleTopic)));
        Topics topic = topicsRepository.findAllByTitle(titleTopic);
        mvc.perform(MockMvcRequestBuilders.post("/removetopic").param("remove", Long.toString(topic.getId())).accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.not(CoreMatchers.containsString(titleTopic))));
    }

    /**
     * Test of addTopic method, of class ForumController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testAddTopic() throws Exception {
        System.out.println("addTopic");
        String titleTopic = "my new topic test";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", titleTopic);
        params.add("create", "");
        params.add("user", "");
        mvc.perform(MockMvcRequestBuilders.post("/addtopic").params(params).accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString(titleTopic)));
    }

    /**
     * Test of errorpage method, of class ForumController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testErrorpage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/error").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Simple forum error page")));
    }

}
