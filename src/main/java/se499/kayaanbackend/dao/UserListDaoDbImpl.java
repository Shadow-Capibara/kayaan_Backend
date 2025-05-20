package se499.kayaanbackend.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.entity.UserList;
import se499.kayaanbackend.repository.UserListRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Profile("db")
public class UserListDaoDbImpl implements UserListDao {
    final UserListRepository userRepository;
    private final UserListRepository userListRepository;

    //    @Override
    public Integer getUserSize(){
        return Math.toIntExact(userRepository.count());
    }

//    @Override
    public Page<UserList> getUsers(Integer pageSize, Integer page){
        return userListRepository.findAll(PageRequest.of(page-1, pageSize));
    }

//    @Override
    public UserList getUser(Integer id){
        return userRepository.findById(id).orElse(null);
    }
}
