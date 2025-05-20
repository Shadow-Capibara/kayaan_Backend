package se499.kayaanbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import se499.kayaanbackend.entity.UserList;
import se499.kayaanbackend.repository.UserListRepository;

@Component
@RequiredArgsConstructor
public class InitApp implements ApplicationListener<ApplicationReadyEvent> {
    final UserListRepository userListRepository;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        userListRepository.save(UserList.builder()
                        .id(1)
                        .firstName("Kayaan")
                        .lastName("Backend")
                        .email("kayaan@gmail.com")
                        .build());
    }
}
