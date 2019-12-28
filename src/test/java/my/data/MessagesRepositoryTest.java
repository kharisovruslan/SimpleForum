/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.data;

import java.util.Date;
import java.util.List;
import my.SimpleForumApplication;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Kharisov Ruslan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SimpleForumApplication.class)
public class MessagesRepositoryTest {

    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    TopicsRepository topicsRepository;
    @Autowired
    ApplicationContext context;
    Topics t;

    public MessagesRepositoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Messages message1 = context.getBean(Messages.class, "message text 1", null, new Date(2019, 12, 19), null);
        Messages message2 = context.getBean(Messages.class, "message text 2", null, new Date(2019, 12, 21), null);
        Messages message3 = context.getBean(Messages.class, "message text 3", null, new Date(2019, 12, 20), null);
        Messages message4 = context.getBean(Messages.class, "message text 4", null, new Date(2019, 12, 25), null);
        Messages message5 = context.getBean(Messages.class, "message text 5", null, new Date(2019, 12, 29), null);
        messagesRepository.save(message1);
        messagesRepository.save(message2);
        messagesRepository.save(message3);
        messagesRepository.save(message4);
        messagesRepository.save(message5);
        Topics topic = context.getBean(Topics.class, "title1", new Date(), null);
        topicsRepository.save(topic);
        message4.setTopic(topic);
        message5.setTopic(topic);
        messagesRepository.save(message4);
        messagesRepository.save(message5);
        t = topic;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findAllByTopic method, of class MessagesRepository.
     */
    @Test
    public void testFindAllByTopic() {
        System.out.println("findAllByTopic");
        List<Messages> l = messagesRepository.findAllByTopicOrderByCreateAsc(null);
        assertEquals(l.get(0).getCreate().getDate(), 19);
        assertEquals(l.get(1).getCreate().getDate(), 20);
        assertEquals(l.get(2).getCreate().getDate(), 21);
        assertEquals(l.size(), 3);
    }

    /**
     * Test of deleteByTopic method, of class MessagesRepository.
     */
    @Test
    public void testDeleteByTopic() {
        System.out.println("deleteByTopic");
        List<Messages> lt = messagesRepository.findAllByTopicOrderByCreateAsc(t);
        assertEquals(messagesRepository.count(), 10);
        topicsRepository.save(t);
        List<Messages> l = messagesRepository.deleteByTopic(t);
        assertEquals(l.size(), 2);
        long c = messagesRepository.count();
        assertEquals(messagesRepository.count(), 8);
    }
}
