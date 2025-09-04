package com.double_o.dambap.review.application;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.auth.service.AuthValidationUtils;
import com.double_o.dambap.common.dto.SuccessResponse;
import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.post.PostInvalidException;
import com.double_o.dambap.exception.review.ReviewInvalidException;
import com.double_o.dambap.post.infrastructure.TaggedUserRepository;
import com.double_o.dambap.recommendation.application.RecommendService;
import com.double_o.dambap.recommendation.dto.response.RecommendResponse;
import com.double_o.dambap.media.Media;
import com.double_o.dambap.media.MediaRepository;
import com.double_o.dambap.post.domain.Type;
import com.double_o.dambap.review.domain.Review;
import com.double_o.dambap.review.dto.request.ReviewRequest;
import com.double_o.dambap.review.dto.response.ReviewResponse;
import com.double_o.dambap.review.dto.response.ReviewInfoResponse;
import com.double_o.dambap.review.dto.response.ReviewPageResponse;
import com.double_o.dambap.review.infrastructure.ReviewRepository;
import com.double_o.dambap.user.application.UserValidationService;
import com.double_o.dambap.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserValidationService userValidationService;
    private final TaggedUserRepository taggedUserRepository;
    private final ReviewRepository reviewRepository;
    private final MediaRepository mediaRepository;
    private final RecommendService recommendService;

    /**
     * 리뷰 등록
     */
    @Transactional
    public ReviewResponse createReview(AuthUser user, ReviewRequest request) {

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());
        
        // 사용자가 원 게시글에 태그된 유저인지 검사
        throwIfUserNotTagged(user, request);

        Review review = Review.builder()
                .content(request.getContent())
                .isPublic(request.isPublic())
                .receivedAt(request.getReceivedAt())
                .isFinished(request.isFinished())
                .recommendationCnt(0)
                .writerId(findUser.getId())
                .originPostId(request.getOriginPostId())
                .build();

        Review savedReview = reviewRepository.save(review);

        String mediaUrl = saveAndGetMediaUrl(request, savedReview);

        return getReviewResponse(savedReview, mediaUrl);
    }

    /**
     * 리뷰 조회
     */
    public ReviewResponse getReview(Long reviewId) {

        Review review = getReviewOrThrowIfNotExist(reviewId);

        return getReviewResponse(review, getMediaUrl(review));
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewResponse editReview(AuthUser user, ReviewRequest request, Long reviewId) {

        Review findReview = getReviewOrThrowIfNotExist(reviewId);

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        AuthValidationUtils.verifySameUser(findUser.getId(), findReview.getWriterId());

        findReview.update(request.getContent(), request.isPublic(), request.getReceivedAt(),
                request.isFinished());

        String mediaUrl = saveAndGetMediaUrl(request, findReview);

        return getReviewResponse(findReview, mediaUrl);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public SuccessResponse deleteReview(AuthUser user, Long reviewId) {

        Review review = getReviewOrThrowIfNotExist(reviewId);

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        AuthValidationUtils.verifySameUser(findUser.getId(), review.getWriterId());

        reviewRepository.deleteById(review.getId());

        return new SuccessResponse("성공적으로 삭제되었습니다.");
    }

    /**
     * 리뷰 좋아요
     */
    @Transactional
    public RecommendResponse updateReviewRecommendStatus(AuthUser user, Long reviewId) {

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        Review findReview = getReviewOrThrowIfNotExist(reviewId);

        recommendService.updateRecommendStatus(findReview, findUser, Type.REVIEW);

        return RecommendResponse.toResponse(findReview.getId(), findReview.getRecommendationCnt());
    }

    /**
     * 리뷰 추천 수 조회
     */
    public RecommendResponse getRecommendationCnt(Long reviewId) {

        Review findReview = getReviewOrThrowIfNotExist(reviewId);

        return RecommendResponse.toResponse(findReview.getId(), findReview.getRecommendationCnt());
    }

    /**
     * 다 먹었는지 여부 토글
     */
    @Transactional
    public SuccessResponse updateFinishedStatus(AuthUser user, Long reviewId) {

        Review findReview = getReviewOrThrowIfNotExist(reviewId);

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        AuthValidationUtils.verifySameUser(findUser.getId(), findReview.getWriterId());

        findReview.changeFinishedStatus();

        return new SuccessResponse("다 먹었는지 여부가 변경되었습니다.");
    }

    /**
     * 공개 여부 토글
     */
    @Transactional
    public SuccessResponse updatePublicStatus(AuthUser user, Long reviewId) {

        Review findReview = getReviewOrThrowIfNotExist(reviewId);

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        AuthValidationUtils.verifySameUser(findUser.getId(), findReview.getWriterId());

        findReview.changePublicity();

        return new SuccessResponse("공개 여부가 변경되었습니다.");
    }

    /**
     * 내가 작성한 리뷰 목록 조회
     */
    public ReviewPageResponse getMyReviewList(AuthUser user, Pageable pageable) {

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        Page<Review> findAllMyReview = reviewRepository.findAllByWriterIdOrderByCreatedAtDesc(
                findUser.getId(), pageable);

        return ReviewPageResponse.toResponse(
                pageable.getPageNumber(),
                findAllMyReview.map(this::convertToReviewInfoResponse).toList());
    }

    /**
     * 전체 리뷰 목록 조회
     */
    public ReviewPageResponse getReviewList(Pageable pageable) {

        Page<Review> findAllReviews = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);

        return ReviewPageResponse.toResponse(
                pageable.getPageNumber(),
                findAllReviews.map(this::convertToReviewInfoResponse).toList()
        );
    }


    // 리뷰 응답 형변환
    private ReviewResponse getReviewResponse(Review review, String mediaUrl) {
        return ReviewResponse.toResponse(
                review.getId(),
                review.getCreatedAt(),
                review.getModifiedAt(),
                review.getOriginPostId(),
                review.getReceivedAt(),
                review.getContent(),
                mediaUrl,
                review.isPublic(),
                review.isFinished(),
                review.getRecommendationCnt()
        );
    }

    private void throwIfUserNotTagged(AuthUser user, ReviewRequest request) {
        if (taggedUserRepository.findByPostIdAndTaggedUserId(
                request.getOriginPostId(), user.getId()).isEmpty()
        ) {
            throw new ReviewInvalidException(ErrorType.NOT_TAGGED_USER_ERROR);
        }
    }

    // 게시글 생성 및 수정 시 미디어 등록
    private String saveAndGetMediaUrl(ReviewRequest request, Review review) {

        // 기존 등록된 게시글 관련 미디어 전부 삭제
        mediaRepository.deleteByTargetId(review.getId());

        Media savedMedia = mediaRepository.save(Media.builder()
                .targetId(review.getId())
                .mediaUrl(request.getMediaUrl())
                .type(Type.REVIEW)
                .sequence(0)
                .build());

        return savedMedia.getMediaUrl();
    }

    // 리뷰 연관 미디어 조회
    private String getMediaUrl(Review review) {
        return mediaRepository.findByTargetId(review.getId()).getMediaUrl();
    }

    // 게시글 정보 응답 dto 로 변환
    private ReviewInfoResponse convertToReviewInfoResponse(Review review) {
        Media thumbnailMedia = mediaRepository.findByTargetId(review.getId());
        return ReviewInfoResponse.toResponse(review.getId(), review.getContent(),
                thumbnailMedia.getMediaUrl());
    }

    // 리뷰 반환, 없으면 예외처리
    private Review getReviewOrThrowIfNotExist(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR));
    }
}
