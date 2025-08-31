package com.double_o.dambap.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLikeResponse {

    private Long postId;
    private int likeCnt;

    public static PostLikeResponse toResponse(
            Long postId, int likeCnt
    ) {
        return new PostLikeResponse(postId, likeCnt);
    }
}
