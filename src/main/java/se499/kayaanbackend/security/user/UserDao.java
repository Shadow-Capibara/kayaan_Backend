package se499.kayaanbackend.security.user;

import java.util.Optional;

public interface UserDao {
    User findByUsername(String username);

    User save(User user);
    Optional<User> findById(Integer id);
}