package com.qnaverse.QnAverse.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    // For convenience, we can find by tagName:
    Optional<Tag> findByTagNameIgnoreCase(String tagName);
}
