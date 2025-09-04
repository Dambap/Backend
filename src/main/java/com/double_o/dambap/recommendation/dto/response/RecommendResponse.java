package com.double_o.dambap.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendResponse {

    private Long targetId;
    private int recommendationCnt;

    public static RecommendResponse toResponse(
            Long targetId, int recommendationCnt
    ) {
        return new RecommendResponse(targetId, recommendationCnt);
    }
}
