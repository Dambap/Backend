package com.double_o.dambap.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostPageResponse {

    private int page;
    private List<PostInfoResponse> postInfoResponses;

    public static PostPageResponse toResponse(int page, List<PostInfoResponse> responses) {
        return new PostPageResponse(page, responses);
    }
}
