package com.double_o.dambap.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewInfoResponse {

    private Long id;
    private String content;
    private String thumbnailUrl;

    public static ReviewInfoResponse toResponse(Long id, String content, String thumbnailUrl) {
        return new ReviewInfoResponse(id, content, thumbnailUrl);
    }

}
