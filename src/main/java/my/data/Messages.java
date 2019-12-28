/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kharisov Ruslan
 */
@Entity
@Table(name = "Messages")
@Component
@Scope("prototype")
@Data
@ToString
public class Messages implements Serializable {

    public Messages() {
    }

    public Messages(String text, Topics topic, Date create, Users user) {
        this.text = text;
        this.topic = topic;
        this.create = create;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 10, max = 2048, message = "size between 10 and 2048")
    @Column(name = "mtext", length = 2048)
    private String text;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topics topic;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}
