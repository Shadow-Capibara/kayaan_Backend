package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import se499.kayaanbackend.entity.UserList;

import java.util.List;

public interface UserListRepository extends JpaRepository<UserList, Integer> {
    List<UserList> findAll();
}
