package com.saksham.Aistudyplatform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.Aistudyplatform.dto.TopicRequest;
import com.saksham.Aistudyplatform.dto.TopicResponse;
import com.saksham.Aistudyplatform.service.TopicService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public TopicResponse createTopic(@Valid @RequestBody TopicRequest request) {
        return topicService.createTopic(request);
    }

    @GetMapping
    public List<TopicResponse> getAllTopics() {
        return topicService.getAllTopics();
    }

    @GetMapping("/{id}")
    public TopicResponse getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id);
    }

    @PutMapping("/{id}")
    public TopicResponse updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest request) {
        return topicService.updateTopic(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
    }
}