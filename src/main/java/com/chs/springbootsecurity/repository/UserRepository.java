package com.chs.springbootsecurity.repository;

import com.chs.springbootsecurity.entity.DAOUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<DAOUser, Long> {
    DAOUser findByUsername(String username);
}