package se499.kayaanbackend.dao;

import se499.kayaanbackend.entity.UserList;
import org.springframework.data.domain.Page;

public interface UserListDao {
    Integer getUserListSize();
    Page<UserList> getUserList(Integer pageSize, Integer page);
    UserList getUserList(Integer id);
    UserList save(UserList userList);
}
