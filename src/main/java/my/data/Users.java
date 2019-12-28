/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kharisov Ruslan
 */
@Entity
@Table(name = "Users")
@Component
@Scope("prototype")
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 30, message = "size between 3 and 30")
    @Column(name = "username", length = 30)
    private String username;

    @NotBlank(message = "surname is required")
    @Size(min = 0, max = 50, message = "size between 0 and 30")
    @Column(name = "surname", length = 30)
    private String surname;

    @NotBlank(message = "firstname is required")
    @Size(min = 0, max = 30, message = "size between 0 and 30")
    @Column(name = "firstname", length = 30)
    private String firstname;

    @NotBlank(message = "secondname is required")
    @Size(min = 0, max = 30, message = "size between 0 and 30")
    @Column(name = "secondname", length = 30)
    private String secondname;

    @NotBlank(message = "email is required")
    @Size(min = 5, max = 50, message = "size between 5 and 50")
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "password", length = 90)
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    public Users(String username, String surname, String firstname, String secondname, String email, String password, boolean enabled) {
        this.username = username;
        this.surname = surname;
        this.firstname = firstname;
        this.secondname = secondname;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Users() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Users{" + "id=" + id + ", username=" + username + ", surname=" + surname + ", firstname=" + firstname + ", secondname=" + secondname + ", email=" + email + ", password=" + password + ", enabled=" + enabled + '}';
    }
}
