package com.ensam.library.authen.service.repository;

import com.ensam.library.authen.service.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
}