package com.double_o.dambap.post.dto.response;

import java.util.List;

// 불변 데이터 전달 객체에 최적화된 record 이용
// record 는 모든 필드를 파라미터로 받는 생성자를 내장하여 @AllArgsConstructor 가 필요 없음
public record PostPageResponse(
        int page,
        List<PostInfoResponse> postInfoResponses
) {
    public static PostPageResponse toResponse(int page, List<PostInfoResponse> responses) {
        return new PostPageResponse(page, responses);
    }
}