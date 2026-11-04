package com.saksham.Aistudyplatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@NotBlank(message = "Subject cannot be empty")
    private String subject;
    @NotBlank(message = "Topic cannot be empty")
    private String topic;

    public Topic() {
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}