/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.data;

import java.util.Date;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author Kharisov Ruslan
 */
public interface TopicsRepository extends PagingAndSortingRepository<Topics, Long> {

    public List<Topics> findAllByCreateAfter(Date startCreate);

    public Topics findAllByTitle(String title);

    public List<Topics> findByOrderByCreateAsc();
}
