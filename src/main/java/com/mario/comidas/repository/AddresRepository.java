package com.mario.comidas.repository;

import com.mario.comidas.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddresRepository extends JpaRepository <Address, Long> {

}
