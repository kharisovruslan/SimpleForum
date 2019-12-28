/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.controller;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import my.data.UserAuthorities;
import my.data.UserAuthoritiesRepository;
import my.data.Users;
import my.data.UsersRepository;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kharisov Ruslan
 */
@WebMvcTest(UsersController.class)
@RunWith(SpringRunner.class)
public class UsersControllerTest {

    @ComponentScan(basePackages = {"my"})
    @TestConfiguration
    @EnableJpaRepositories(basePackages = "my")
    @EnableTransactionManagement
    static class myConfig {

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(emf);
            return transactionManager;
        }

        @Bean
        Properties additionalProperties() {
            Properties properties = new Properties();
            properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            return properties;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(getDataSource());
            em.setPackagesToScan(new String[]{"my.data"});
            JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            em.setJpaVendorAdapter(vendorAdapter);
            em.setJpaProperties(additionalProperties());
            return em;
        }

        @Bean
        public DataSource getDataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
            dataSource.setUsername("sa");
            dataSource.setPassword("sa");
            return dataSource;
        }
    }

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

    @Before
    public void setUp() {
        if (usersRepository.count() == 0) {
            Users user = new Users("user", "surname", "firstname", "secondname", "user@mail.org", encoder.encode("12345"), true);
            Users admin = new Users("admin", "surname", "firstname", "secondname", "admin@mail.org", encoder.encode("12345"), true);
            usersRepository.save(user);
            usersRepository.save(admin);
            userAuthoritiesRepository.save(new UserAuthorities(user, "ROLE_USER"));
            userAuthoritiesRepository.save(new UserAuthorities(admin, "ROLE_ADMIN"));
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of login method, of class UsersController.
     */
    @Test
    public void testLogin() throws Exception {
        System.out.println("login");
        mvc.perform(MockMvcRequestBuilders.get("/login").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("username")));
    }

    /**
     * Test of profile method, of class UsersController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testProfile() throws Exception {
        System.out.println("testProfile");
        mvc.perform(MockMvcRequestBuilders.get("/profile").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("user@mail.org")));
    }

    /**
     * Test of userprofile method, of class UsersController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testUserprofile() throws Exception {
        System.out.println("testUserprofile");
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/2").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("admin@mail.org")));
    }

    /**
     * Test of profileSave method, of class UsersController.
     */
    @WithMockUser(username = "user")
    @Test
    public void testProfileSave() throws Exception {
        System.out.println("profileSave");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", "user@mail.localhost");
        params.add("firstname", "firstname");
        params.add("secondname", "secondname");
        params.add("surname", "surname");
        params.add("username", "user");
        params.add("password", "12345");
        mvc.perform(MockMvcRequestBuilders.post("/profile").params(params).accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/")).andExpect(status().isFound());
        Users u = usersRepository.findByUsername("user");
        assertEquals(u.getEmail(), "user@mail.localhost");
    }

    /**
     * Test of register method, of class UsersController.
     */
    @Test
    public void testRegister() throws Exception {
        System.out.println("register");
        mvc.perform(MockMvcRequestBuilders.get("/register").accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Simple forum register new user")));
    }

    /**
     * Test of registerNewUser method, of class UsersController.
     */
    @Test
    public void testRegisterNewUser() throws Exception {
        System.out.println("profileSave");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", "userNew@mail.localhost");
        params.add("firstname", "firstname");
        params.add("secondname", "secondname");
        params.add("surname", "surname");
        params.add("username", "userNew");
        params.add("password", "12345");
        mvc.perform(MockMvcRequestBuilders.post("/register").params(params).accept(MediaType.TEXT_HTML)).andDo(print())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/")).andExpect(status().isFound());
        Users u = usersRepository.findByUsername("userNew");
        assertEquals(u.getEmail(), "userNew@mail.localhost");
    }
}
