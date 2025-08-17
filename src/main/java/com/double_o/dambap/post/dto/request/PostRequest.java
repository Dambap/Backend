package com.double_o.dambap.post.dto.request;

import com.double_o.dambap.post.domain.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 게시글 등록 dto
 */
@Data
@Builder
public class PostRequest {

    private Category category;

    private LocalDate manufactureDate;

    private int expireDays;

    private String content;

    @NotNull(message = "최소 한 장 이상의 이미지를 등록해야합니다.")
    private List<String> mediaUrls;

    private List<Long> taggedUserIds;

    @JsonProperty("isPublic")
    private boolean isPublic;
}
