package com.saksham.Aistudyplatform.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.saksham.Aistudyplatform.dto.TopicRequest;
import com.saksham.Aistudyplatform.entity.AppUser;
import com.saksham.Aistudyplatform.entity.Topic;
import com.saksham.Aistudyplatform.exception.TopicNotFoundException;
import com.saksham.Aistudyplatform.repository.AppUserRepository;
import com.saksham.Aistudyplatform.repository.TopicRepository;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    private static final String USER_EMAIL = "student@example.com";

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private TopicService topicService;

    @BeforeEach
    void authenticateTestUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_EMAIL, null, List.of())
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTopicsReturnsOnlyTopicsForAuthenticatedUser() {
        Topic topic = topic("Java", "OOP");
        when(topicRepository.findByUserEmail(USER_EMAIL)).thenReturn(List.of(topic));

        var result = topicService.getAllTopics();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getSubject());
        assertEquals("OOP", result.get(0).getTopic());
        verify(topicRepository).findByUserEmail(USER_EMAIL);
    }

    @Test
    void getAllTopicsReturnsMultipleOwnedTopics() {
        when(topicRepository.findByUserEmail(USER_EMAIL)).thenReturn(List.of(
                topic("Java", "OOP"),
                topic("DBMS", "Normalization")
        ));

        var result = topicService.getAllTopics();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getSubject());
        assertEquals("DBMS", result.get(1).getSubject());
        verify(topicRepository).findByUserEmail(USER_EMAIL);
    }

    @Test
    void getTopicByIdFindsTopicForAuthenticatedUser() {
        when(topicRepository.findByIdAndUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.of(topic("Java", "OOP")));

        var result = topicService.getTopicById(1L);

        assertNotNull(result);
        assertEquals("Java", result.getSubject());
        assertEquals("OOP", result.getTopic());
        verify(topicRepository).findByIdAndUserEmail(1L, USER_EMAIL);
    }

    @Test
    void getTopicByIdThrowsWhenTopicIsNotOwnedOrMissing() {
        when(topicRepository.findByIdAndUserEmail(99L, USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class,
                () -> topicService.getTopicById(99L));
        verify(topicRepository).findByIdAndUserEmail(99L, USER_EMAIL);
    }

    @Test
    void createTopicAssignsAuthenticatedUserAsOwner() {
        AppUser user = new AppUser("Student", USER_EMAIL, "encoded-password");
        when(appUserRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(topicRepository.save(any(Topic.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = topicService.createTopic(topicRequest("Java", "OOP"));

        assertNotNull(result);
        assertEquals("Java", result.getSubject());
        assertEquals("OOP", result.getTopic());
        verify(appUserRepository).findByEmail(USER_EMAIL);
        ArgumentCaptor<Topic> savedTopic = ArgumentCaptor.forClass(Topic.class);
        verify(topicRepository).save(savedTopic.capture());
        assertEquals(user, savedTopic.getValue().getUser());
    }

    @Test
    void updateTopicChangesOnlyOwnedTopic() {
        Topic existingTopic = topic("Java", "OOP");
        when(topicRepository.findByIdAndUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.of(existingTopic));
        when(topicRepository.save(any(Topic.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = topicService.updateTopic(1L, topicRequest("Java", "Collections"));

        assertNotNull(result);
        assertEquals("Java", result.getSubject());
        assertEquals("Collections", result.getTopic());
        verify(topicRepository).findByIdAndUserEmail(1L, USER_EMAIL);
        verify(topicRepository).save(existingTopic);
    }

    @Test
    void updateTopicRejectsTopicNotOwnedByAuthenticatedUser() {
        when(topicRepository.findByIdAndUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class,
                () -> topicService.updateTopic(1L, topicRequest("Java", "Collections")));

        verify(topicRepository).findByIdAndUserEmail(1L, USER_EMAIL);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void deleteTopicDeletesOnlyOwnedTopic() {
        Topic topic = topic("Java", "OOP");
        when(topicRepository.findByIdAndUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.of(topic));

        topicService.deleteTopic(1L);

        verify(topicRepository).findByIdAndUserEmail(1L, USER_EMAIL);
        verify(topicRepository).delete(topic);
    }

    @Test
    void deleteTopicRejectsTopicNotOwnedByAuthenticatedUser() {
        when(topicRepository.findByIdAndUserEmail(1L, USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class,
                () -> topicService.deleteTopic(1L));

        verify(topicRepository).findByIdAndUserEmail(1L, USER_EMAIL);
        verify(topicRepository, never()).delete(any(Topic.class));
    }

    private Topic topic(String subject, String name) {
        Topic topic = new Topic();
        topic.setSubject(subject);
        topic.setTopic(name);
        return topic;
    }

    private TopicRequest topicRequest(String subject, String name) {
        TopicRequest request = new TopicRequest();
        request.setSubject(subject);
        request.setTopic(name);
        return request;
    }
}
