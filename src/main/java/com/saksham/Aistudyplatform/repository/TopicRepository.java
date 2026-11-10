package com.saksham.Aistudyplatform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saksham.Aistudyplatform.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByUserEmail(String email);
    Optional<Topic> findByIdAndUserEmail(Long id, String email);
}