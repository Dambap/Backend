package com.double_o.dambap.review.presentation;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.common.model.ResponseDto;
import com.double_o.dambap.review.application.ReviewService;
import com.double_o.dambap.review.dto.request.ReviewRequest;
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
@RequestMapping(path = "/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "나눔 받은 음식 리뷰 등록")
    @PostMapping
    public ResponseEntity<?> createReview(
            AuthUser user,
            @Parameter(required = true, description = "리뷰 생성 요청")
            @RequestBody @Valid ReviewRequest request
    ) {
        var response = reviewService.createReview(user, request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "나눔 받은 음식 리뷰 조회")
    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<?> getReview(
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId
    ) {
        var response = reviewService.getReview(reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "나눔 받은 음식 리뷰 수정")
    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<?> editReview(
            AuthUser user,
            @Parameter(required = true, description = "리뷰 수정 요청")
            @RequestBody @Valid ReviewRequest request,
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId
    ) {
        var response = reviewService.editReview(user, request, reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "나눔 받은 음식 리뷰 삭제")
    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<?> deleteReview(
            AuthUser user,
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId
    ) {
        var response = reviewService.deleteReview(user, reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "리뷰 추천")
    @PostMapping(path = "/{reviewId}/recommendation")
    public ResponseEntity<?> recommendReview(
            AuthUser user,
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId
    ) {
        var response = reviewService.updateReviewRecommendStatus(user, reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "리뷰 추천 수 조회")
    @GetMapping(path = "/{reviewId}/recommendation")
    public ResponseEntity<?> getRecommendCnt(
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId) {
        var response = reviewService.getRecommendationCnt(reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "완식 여부 수정")
    @PatchMapping(path = "/{reviewId}/finish")
    public ResponseEntity<?> updateFinishedStatus(
            AuthUser user,
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId
    ) {
        var response = reviewService.updateFinishedStatus(user, reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "리뷰 공개여부 전환")
    @PatchMapping(path = "/{reviewId}/publicity")
    public ResponseEntity<?> updatePublicStatus(
            AuthUser user,
            @Parameter(description = "리뷰 고유 번호")
            @PathVariable("reviewId") Long reviewId
    ) {
        var response = reviewService.updatePublicStatus(user, reviewId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "내 리뷰 목록 조회")
    @GetMapping(path = "/all-my-reviews")
    public ResponseEntity<?> getMyReviewList(
            AuthUser user,
            @Parameter(description = "한 페이지의 데이터 개수")
            @PageableDefault(size = 12) Pageable pageable
    ) {
        var response = reviewService.getMyReviewList(user, pageable);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "전체 리뷰 목록 조회")
    @GetMapping(path = "/all-latest-reviews")
    public ResponseEntity<?> getReviewList(
            @Parameter(description = "한 페이지의 데이터 개수")
            @PageableDefault(size = 12) Pageable pageable
    ) {
        var response = reviewService.getReviewList(pageable);
        return ResponseDto.ok(response);
    }
}
