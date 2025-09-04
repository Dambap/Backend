package com.double_o.dambap.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponse {

    private Long postId;
    private int likeCnt;

    public static LikeResponse toResponse(
            Long postId, int likeCnt
    ) {
        return new LikeResponse(postId, likeCnt);
    }
}
