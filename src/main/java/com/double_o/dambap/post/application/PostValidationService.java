package com.double_o.dambap.post.application;

import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.post.PostInvalidException;
import com.double_o.dambap.post.domain.Post;
import com.double_o.dambap.post.infrastructure.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidationService {

    private final PostRepository postRepository;

    // 게시글 반환, 없으면 예외처리
    public Post getPostOrThrowIfNotExist(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR)
        );
    }
}
