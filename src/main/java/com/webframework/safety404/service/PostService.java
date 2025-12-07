package com.webframework.safety404.service;

import com.webframework.safety404.domain.Post;
import com.webframework.safety404.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 전체 목록 조회 (고정글 먼저 정렬)
    public List<Post> findAll() {
        return postRepository.findAllByOrderByPinnedDescCreatedAtDesc();
    }

    // 게시글 1개 조회
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }

    // 저장 (새 글 / 수정 공통)
    public Post save(Post post) {

        // 화면 표시용 writer가 비어있으면 기본값
        if (post.getWriter() == null || post.getWriter().trim().isEmpty()) {
            post.setWriter("비회원");
        }

        return postRepository.save(post);
    }

    // 검색 기능
    public List<Post> search(String type, String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        if (type == null || type.equals("all")) {
            return postRepository
                    .findByTitleContainingOrContentContainingOrderByPinnedDescCreatedAtDesc(keyword, keyword);
        }

        switch (type) {
            case "title":
                return postRepository
                        .findByTitleContainingOrderByPinnedDescCreatedAtDesc(keyword);

            case "writer":
                return postRepository
                        .findByWriterContainingOrderByPinnedDescCreatedAtDesc(keyword);

            default:
                return findAll();
        }
    }

    // 게시글 삭제
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}
