package se499.kayaanbackend.security.entity;

import jakarta.transaction.Transactional;

public interface UserService {
    User save(User user);

    @Transactional
    User findByUsername(String username);
}