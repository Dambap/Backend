package com.double_o.dambap.review.dto.response;

import java.util.List;

public record ReviewPageResponse(
        int page,
        List<ReviewInfoResponse> reviewInfoResponses
) {
    public static ReviewPageResponse toResponse(int page, List<ReviewInfoResponse> responses) {
        return new ReviewPageResponse(page, responses);
    }
}
