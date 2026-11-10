package com.saksham.Aistudyplatform.service;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saksham.Aistudyplatform.dto.TopicRequest;
import com.saksham.Aistudyplatform.dto.TopicResponse;
import com.saksham.Aistudyplatform.entity.AppUser;
import com.saksham.Aistudyplatform.entity.Topic;
import com.saksham.Aistudyplatform.exception.TopicNotFoundException;
import com.saksham.Aistudyplatform.repository.AppUserRepository;
import com.saksham.Aistudyplatform.repository.TopicRepository;
@Service
public class TopicService {

    private final TopicRepository topicRepository;
private final AppUserRepository appUserRepository;

public TopicService(TopicRepository topicRepository, AppUserRepository appUserRepository) {
    this.topicRepository = topicRepository;
    this.appUserRepository = appUserRepository;
}

    public TopicResponse createTopic(TopicRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    AppUser user = appUserRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Topic topic = new Topic();
    topic.setSubject(request.getSubject());
    topic.setTopic(request.getTopic());
    topic.setUser(user);

    Topic savedTopic = topicRepository.save(topic);

    return convertToResponse(savedTopic);
}

    public List<TopicResponse> getAllTopics() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    return topicRepository.findByUserEmail(email)
            .stream()
            .map(this::convertToResponse)
            .toList();
}

    public TopicResponse getTopicById(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    Topic topic = topicRepository.findByIdAndUserEmail(id, email)
            .orElseThrow(() -> new TopicNotFoundException(id));

    return convertToResponse(topic);
}

    public TopicResponse updateTopic(Long id, TopicRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    Topic topic = topicRepository.findByIdAndUserEmail(id, email)
            .orElseThrow(() -> new TopicNotFoundException(id));

    topic.setSubject(request.getSubject());
    topic.setTopic(request.getTopic());

    Topic updatedTopic = topicRepository.save(topic);

    return convertToResponse(updatedTopic);
}

   public void deleteTopic(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    Topic topic = topicRepository.findByIdAndUserEmail(id, email)
            .orElseThrow(() -> new TopicNotFoundException(id));

    topicRepository.delete(topic);
}

    private TopicResponse convertToResponse(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getSubject(),
                topic.getTopic()
        );
    }
    
}