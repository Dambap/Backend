package com.double_o.dambap.review.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 리뷰 단건 조회 dto
 */
@Data
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long originPostId;
    private LocalDate receivedAt;
    private String content;
    private String mediaUrl;
    private boolean isPublic;
    private boolean isFinished;
    private int recommendationCnt;

    public static ReviewResponse toResponse(
            Long id,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            Long originPostId,
            LocalDate receivedAt,
            String content,
            String mediaUrl,
            boolean isPublic,
            boolean isFinished,
            int recommendationCnt
    ) {
        return new ReviewResponse(id, createdAt, modifiedAt, originPostId, receivedAt,
                content, mediaUrl, isPublic, isFinished, recommendationCnt);
    }
}
