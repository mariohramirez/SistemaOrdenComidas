package com.mario.comidas.repository;

import com.mario.comidas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {

    public User findByEmail(String username);


}
