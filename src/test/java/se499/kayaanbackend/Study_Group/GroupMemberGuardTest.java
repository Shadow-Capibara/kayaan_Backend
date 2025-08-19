package se499.kayaanbackend.Study_Group;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import se499.kayaanbackend.Study_Group.repository.GroupContentRepository;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.service.GroupContentServiceImpl;
import se499.kayaanbackend.Study_Group.service.GroupStorageService;

public class GroupMemberGuardTest {

    private GroupContentServiceImpl service;
    private GroupContentRepository contentRepo;
    private GroupMemberRepository memberRepo;
    private GroupStorageService storageService;

    @BeforeEach
    void setup() {
        contentRepo = mock(GroupContentRepository.class);
        memberRepo = mock(GroupMemberRepository.class);
        storageService = mock(GroupStorageService.class);
        service = new GroupContentServiceImpl(contentRepo, memberRepo, storageService);
    }

    @Test
    void listResources_forbiddenWhenNotMember() {
        when(memberRepo.existsByGroupIdAndUserId(10, 1)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.listResources(1, 10));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void getPreviewUrl_forbiddenWhenNotMember() {
        when(memberRepo.existsByGroupIdAndUserId(10, 1)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getPreviewUrl(1, 10, 99L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void getPreviewUrl_notFoundWhenMissing() {
        when(memberRepo.existsByGroupIdAndUserId(10, 1)).thenReturn(true);
        when(contentRepo.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getPreviewUrl(1, 10, 99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}


