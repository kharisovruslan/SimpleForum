/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.data;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Kharisov Ruslan
 */
public interface MessagesRepository extends CrudRepository<Messages, Long> {

    public List<Messages> findAllByTopicOrderByCreateAsc(Topics topic);

    public Messages findByTopicAndText(Topics topic, String text);

    @Transactional
    public List<Messages> deleteByTopic(Topics topic);
}
