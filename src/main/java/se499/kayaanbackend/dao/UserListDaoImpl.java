package se499.kayaanbackend.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.entity.UserList;

import java.util.List;

@Repository
@Profile("manual")
public class UserListDaoImpl implements UserListDao {
    Page<UserList> userLists;
}
