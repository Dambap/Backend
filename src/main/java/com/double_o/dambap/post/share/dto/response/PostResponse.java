package com.double_o.dambap.post.share.dto.response;

import com.double_o.dambap.post.share.domain.Category;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 게시글 단건 조회 dto
 */
@Data
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Category category;
    private LocalDate manufactureDate;
    private int expireDays;
    private String content;
    private List<String> mediaUrls;
    private List<Long> taggedUserIds;
    private boolean isPublic;
    private int likeCnt;
    private Long writerId;

    public static PostResponse toResponse(
            Long id,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            Category category,
            LocalDate manufactureDate,
            int expireDays,
            String content,
            List<String> mediaUrls,
            List<Long> taggedUserIds,
            boolean isPublic,
            int likeCnt,
            Long writerId
    ) {
        return new PostResponse(id, createdAt, modifiedAt, category, manufactureDate, expireDays,
                content, mediaUrls, taggedUserIds, isPublic, likeCnt, writerId);
    }
}
