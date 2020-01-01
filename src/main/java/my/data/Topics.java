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
import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kharisov Ruslan
 */
@Entity
@Table(name = "TopicsSF")
@Component
@Scope("prototype")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Topics implements Serializable {

    public Topics(String title, Date create, Users user) {
        this.title = title;
        this.create = create;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "size between 5 and 200")
    @Column(name = "ttitle", length = 200)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tcreate")
    private Date create;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
