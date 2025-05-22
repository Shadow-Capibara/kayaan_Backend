package se499.kayaanbackend.security.user;

public interface UserDao {
    User findByUsername(String username);

    User save(User user);
}