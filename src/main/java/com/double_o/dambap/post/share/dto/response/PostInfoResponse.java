package com.double_o.dambap.post.share.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostInfoResponse {

    private Long id;
    private String content;
    private String thumbnailUrl;

    public static PostInfoResponse toResponse(Long id, String content, String thumbnailUrl) {
        return new PostInfoResponse(id, content, thumbnailUrl);
    }

}
