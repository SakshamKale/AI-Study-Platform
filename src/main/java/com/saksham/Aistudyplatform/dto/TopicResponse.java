package com.saksham.Aistudyplatform.dto;

public class TopicResponse {

    private Long id;
    private String subject;
    private String topic;

    public TopicResponse(Long id, String subject, String topic) {
        this.id = id;
        this.subject = subject;
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }
}