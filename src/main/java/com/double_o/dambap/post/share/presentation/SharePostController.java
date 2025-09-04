package com.double_o.dambap.post.share.presentation;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.common.model.ResponseDto;
import com.double_o.dambap.post.share.application.SharePostService;
import com.double_o.dambap.post.share.dto.request.PostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/posts/shared")
@RequiredArgsConstructor
public class SharePostController {

    private final SharePostService postService;

    @Operation(summary = "게시글 생성")
    @PostMapping
    public ResponseEntity<?> createQuestion(
            AuthUser user,
            @Parameter(required = true, description = "게시글 생성 요청")
            @RequestBody @Valid PostRequest request) {
        var response = postService.createPost(user, request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "게시글 조회")
    @GetMapping(path = "/{postId}")
    public ResponseEntity<?> readQuestion(
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("postId") Long postId) {
        var response = postService.getPost(postId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping(path = "/{postId}")
    public ResponseEntity<?> updateQuestion(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("postId") Long postId,
            @Parameter(required = true, description = "게시글 수정 요청")
            @RequestBody @Valid PostRequest request) {
        var response = postService.editPost(user, postId, request);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<?> deleteQuestion(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("postId") Long postId
    ) {
        var response = postService.deletePost(user, postId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "게시글 추천")
    @PostMapping(path = "/{postId}/recommendation")
    public ResponseEntity<?> recommendQuestion(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("postId") Long postId
    ) {
        var response = postService.updatePostLike(user, postId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "게시글 공개여부 전환")
    @PatchMapping(path = "/{postId}")
    public ResponseEntity<?> changePublicity(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable(name = "postId") Long postId
    ) {
        var response = postService.changePublicity(user, postId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "내가 나눈 음식 게시글 목록 조회")
    @GetMapping(path = "/all-my-shared-post")
    public ResponseEntity<?> getAllMySharedPost(
            AuthUser user,
            @Parameter(description = "한 페이지의 데이터 개수")
            @PageableDefault(size = 12) Pageable pageable
    ) {
        var response = postService.getAllMySharedPost(user, pageable);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "전체 나눈 음식 게시글 목록 조회")
    @GetMapping(path = "/all-latest-post")
    public ResponseEntity<?> getAllLatestPost(
            @Parameter(description = "한 페이지의 데이터 개수")
            @PageableDefault(size = 12) Pageable pageable
    ) {
        var response = postService.getAllLatestPost(pageable);
        return ResponseDto.ok(response);
    }

    // TODO 타인이 등록한 리뷰를 기반으로, ‘내가 나눈 음식 게시글 등록’ 추천 목록 조회
}
