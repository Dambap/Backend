package com.double_o.dambap.post.dto.request;

import com.double_o.dambap.post.domain.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 게시글 등록 dto
 */
@Data
@Builder
public class PostRequest {

    private Category category;

    private LocalDate manufactureDate;

    @Range(min = 1, max = 14, message = "expireDays 는 최소 1, 최대 14 까지 가능합니다.")
    private int expireDays;

    private String content;

    @NotNull(message = "최소 한 장 이상의 이미지를 등록해야합니다.")
    private List<String> mediaUrls;

    private List<Long> taggedUserIds;

    @JsonProperty("isPublic")
    private boolean isPublic;
}
