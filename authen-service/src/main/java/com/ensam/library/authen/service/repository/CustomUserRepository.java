package com.ensam.library.authen.service.repository;

import com.ensam.library.authen.service.model.User;

public interface CustomUserRepository {
    User findByEmail(String email);
}
