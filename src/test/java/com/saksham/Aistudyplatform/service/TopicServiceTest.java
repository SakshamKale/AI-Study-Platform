package com.saksham.Aistudyplatform.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.saksham.Aistudyplatform.dto.TopicRequest;
import com.saksham.Aistudyplatform.entity.Topic;
import com.saksham.Aistudyplatform.exception.TopicNotFoundException;
import com.saksham.Aistudyplatform.repository.TopicRepository;


@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    
    @Test
void getAllTopicsTest() {
    Topic topic = new Topic();
   
    topic.setSubject("Java");
    topic.setTopic("OOP");

    when(topicRepository.findAll()).thenReturn(java.util.List.of(topic));

    var result = topicService.getAllTopics();

    assertEquals(1, result.size());
    assertEquals("Java", result.get(0).getSubject());
    assertEquals("OOP", result.get(0).getTopic());

    verify(topicRepository).findAll();
}
@Test
void getAllTopicsWithMultipleTopicsTest() {
    Topic topic1 = new Topic();
    topic1.setSubject("Java");
    topic1.setTopic("OOP");

    Topic topic2 = new Topic();
    topic2.setSubject("DBMS");
    topic2.setTopic("Normalization");

    when(topicRepository.findAll()).thenReturn(java.util.List.of(topic1, topic2));

    var result = topicService.getAllTopics();

    assertEquals(2, result.size());
    assertEquals("Java", result.get(0).getSubject());
    assertEquals("DBMS", result.get(1).getSubject());

    verify(topicRepository).findAll();
}
@Test
void getTopicByIdTest() {
    Topic topic = new Topic();
    topic.setSubject("Java");
    topic.setTopic("OOP");

    when(topicRepository.findById(1L))
            .thenReturn(java.util.Optional.of(topic));

    var result = topicService.getTopicById(1L);

    assertNotNull(result);
    assertEquals("Java", result.getSubject());
    assertEquals("OOP", result.getTopic());

    verify(topicRepository).findById(1L);
}
@Test
void getTopicByIdNotFoundTest() {
    when(topicRepository.findById(99L))
            .thenReturn(java.util.Optional.empty());

    assertThrows(
            TopicNotFoundException.class,
            () -> topicService.getTopicById(99L)
    );

    verify(topicRepository).findById(99L);
}
@Test
void createTopicTest() {
    TopicRequest request = new TopicRequest();
    request.setSubject("Java");
    request.setTopic("OOP");

    Topic savedTopic = new Topic();
    savedTopic.setSubject("Java");
    savedTopic.setTopic("OOP");

    when(topicRepository.save(any(Topic.class)))
            .thenReturn(savedTopic);

    var result = topicService.createTopic(request);

    assertNotNull(result);
    assertEquals("Java", result.getSubject());
    assertEquals("OOP", result.getTopic());

    verify(topicRepository).save(any(Topic.class));
}
@Test
    void updateTopicTest() {
        Topic existingTopic = new Topic();
        existingTopic.setSubject("Java");
        existingTopic.setTopic("OOP");

        TopicRequest request = new TopicRequest();
        request.setSubject("Java");
        request.setTopic("Collections");

        when(topicRepository.findById(1L))
                .thenReturn(Optional.of(existingTopic));

        when(topicRepository.save(any(Topic.class)))
                .thenReturn(existingTopic);

        var result = topicService.updateTopic(1L, request);

        assertNotNull(result);
        assertEquals("Java", result.getSubject());
        assertEquals("Collections", result.getTopic());

        verify(topicRepository).findById(1L);
        verify(topicRepository).save(any(Topic.class));
    }

    
    @Test
void deleteTopicTest() {
    Topic topic = new Topic();
    

    when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));

    topicService.deleteTopic(1L);

    verify(topicRepository).findById(1L);
    verify(topicRepository).delete(topic);
}
}