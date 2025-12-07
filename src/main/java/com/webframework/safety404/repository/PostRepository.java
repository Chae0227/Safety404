package com.webframework.safety404.repository;

import com.webframework.safety404.domain.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByPinnedDescCreatedAtDesc();

    List<Post> findByTitleContainingOrContentContainingOrderByPinnedDescCreatedAtDesc(String title, String content);

    List<Post> findByTitleContainingOrderByPinnedDescCreatedAtDesc(String title);

    List<Post> findByWriterContainingOrderByPinnedDescCreatedAtDesc(String writer);
}