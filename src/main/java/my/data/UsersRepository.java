/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.data;

import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Kharisov Ruslan
 */
public interface UsersRepository extends CrudRepository<Users, Long> {

    public Users findByUsername(String username);
}
