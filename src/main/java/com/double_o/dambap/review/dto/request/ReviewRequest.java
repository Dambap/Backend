package com.double_o.dambap.review.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReviewRequest {

    @NotNull
    private Long originPostId;

    @NotNull
    private LocalDate receivedAt;

    @NotBlank(message = "본문은 필수입니다.")
    private String content;

    private String mediaUrl;

    @JsonProperty("isPublic")
    private boolean isPublic;

    @JsonProperty("isFinished")
    private boolean isFinished;
}