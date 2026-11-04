package com.saksham.Aistudyplatform;
import com.saksham.Aistudyplatform.Topic;
import com.saksham.Aistudyplatform.TopicRepository;
import com.saksham.Aistudyplatform.TopicNotFoundException;
import com.saksham.Aistudyplatform.dto.TopicRequest;
import com.saksham.Aistudyplatform.dto.TopicResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicResponse createTopic(TopicRequest request) {
        Topic topic = new Topic();
        topic.setSubject(request.getSubject());
        topic.setTopic(request.getTopic());

        Topic savedTopic = topicRepository.save(topic);

        return convertToResponse(savedTopic);
    }

    public List<TopicResponse> getAllTopics() {
        return topicRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public TopicResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        return convertToResponse(topic);
    }

    public TopicResponse updateTopic(Long id, TopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        topic.setSubject(request.getSubject());
        topic.setTopic(request.getTopic());

        Topic updatedTopic = topicRepository.save(topic);

        return convertToResponse(updatedTopic);
    }

    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
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