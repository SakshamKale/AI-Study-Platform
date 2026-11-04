
package com.saksham.Aistudyplatform;

public class TopicNotFoundException extends RuntimeException {

    public TopicNotFoundException(Long id) {
        super("Topic not found with id: " + id);
    }
}
