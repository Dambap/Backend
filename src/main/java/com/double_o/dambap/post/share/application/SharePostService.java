package com.double_o.dambap.post.share.application;

import static com.double_o.dambap.post.share.utils.TaggedUserConstants.TAGGED_USER_MAX_SIZE;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.post.PostInvalidException;
import com.double_o.dambap.post.like.PostLike;
import com.double_o.dambap.post.media.Media;
import com.double_o.dambap.post.share.domain.TaggedUser;
import com.double_o.dambap.post.share.domain.Type;
import com.double_o.dambap.post.share.dto.response.PostInfoResponse;
import com.double_o.dambap.post.like.PostLikeResponse;
import com.double_o.dambap.post.share.dto.response.PostPageResponse;
import com.double_o.dambap.post.like.PostLikeRepository;
import com.double_o.dambap.post.share.domain.SharePost;
import com.double_o.dambap.post.share.dto.request.PostRequest;
import com.double_o.dambap.post.share.dto.response.PostResponse;
import com.double_o.dambap.post.media.MediaRepository;
import com.double_o.dambap.post.share.infrastructure.SharePostRepository;
import com.double_o.dambap.auth.service.AuthValidationUtils;
import com.double_o.dambap.post.share.infrastructure.TaggedUserRepository;
import com.double_o.dambap.user.domain.User;
import com.double_o.dambap.user.application.UserValidationService;
import com.double_o.dambap.common.dto.SuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharePostService {

    private final UserValidationService userValidationService;
    private final SharePostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final TaggedUserRepository taggedUserRepository;
    private final MediaRepository mediaRepository;

    /**
     * 게시글 등록
     */
    @Transactional
    public PostResponse createPost(AuthUser user, PostRequest request) {

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        SharePost post = SharePost.builder()
                .category(request.getCategory())
                .manufactureDate(request.getManufactureDate())
                .expireDays(request.getExpireDays())
                .content(request.getContent())
                .isPublic(request.isPublic())
                .likeCnt(0)
                .writerId(findUser.getId())
                .build();

        SharePost savedPost = postRepository.save(post);

        List<String> mediaUrls = saveAndGetMediaUrls(request, savedPost);

        List<Long> taggedUserIds = saveAndGetTaggedUserIds(request, savedPost);

        return getPostResponse(savedPost, mediaUrls, taggedUserIds);
    }

    /**
     * 게시글 조회
     */
    public PostResponse getPost(Long postId) {

        SharePost findPost = getPostOrThrowIfNotExist(postId);

        List<String> mediaUrls = getMediaUrls(findPost);

        List<Long> taggedUserIds = getTaggedUserIds(findPost);

        return getPostResponse(findPost, mediaUrls, taggedUserIds);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse editPost(AuthUser user, Long postId, PostRequest request) {

        SharePost findPost = getPostOrThrowIfNotExist(postId);

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        AuthValidationUtils.verifySameUser(findUser.getId(), findPost.getWriterId());

        findPost.updateSharePost(request.getCategory(), request.getManufactureDate(),
                request.getExpireDays(), request.getContent(), request.isPublic());

        List<String> mediaUrls = saveAndGetMediaUrls(request, findPost);

        List<Long> taggedUserIds = saveAndGetTaggedUserIds(request, findPost);

        return getPostResponse(findPost, mediaUrls, taggedUserIds);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public SuccessResponse deletePost(AuthUser user, Long postId) {

        SharePost findPost = getPostOrThrowIfNotExist(postId);

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        AuthValidationUtils.verifySameUser(findUser.getId(), findPost.getWriterId());

        postRepository.deleteById(postId);

        return new SuccessResponse("성공적으로 삭제되었습니다.");
    }

    /**
     * 게시글 좋아요
     */
    @Transactional
    public PostLikeResponse updatePostLike(AuthUser user, Long postId) {

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        SharePost findPost = getPostOrThrowIfNotExist(postId);

        Optional<PostLike> postLike = postLikeRepository.findByPostIdAndLikerId(
                findPost.getId(), findUser.getId());

        updateLikeStatus(postLike, findPost, findUser);

        return PostLikeResponse.toResponse(findPost.getId(), findPost.getLikeCnt());
    }

    /**
     * 게시글 공개 여부 전환
     */
    @Transactional
    public SuccessResponse changePublicity(AuthUser user, Long postId) {

        userValidationService.getUserOrThrowIfNotExist(user.getId());

        SharePost findPost = getPostOrThrowIfNotExist(postId);

        findPost.changePublicity();
        return new SuccessResponse("공개여부가 성공적으로 전환되었습니다.");
    }

    /**
     * 내가 나눈 음식 게시글 목록 최신순으로 반환
     */
    public PostPageResponse getAllMySharedPost(AuthUser user, Pageable pageable) {

        User findUser = userValidationService.getUserOrThrowIfNotExist(user.getId());

        Page<SharePost> findAllMySharedPost = postRepository.findAllByWriterIdOrderByCreatedAtDesc(
                findUser.getId(), pageable);

        return PostPageResponse.toResponse(
                pageable.getPageNumber(),
                findAllMySharedPost.map(this::convertToPostInfoResponse).toList());
    }

    /**
     * 전체 게시글 목록 최신순으로 반환
     */
    @Transactional(readOnly = true)
    public PostPageResponse getAllLatestPost(Pageable pageable) {
        Page<SharePost> findAllPosts = postRepository.findAllByOrderByCreatedAtDesc(
                pageable);

        return PostPageResponse.toResponse(
                pageable.getPageNumber(),
                findAllPosts.map(this::convertToPostInfoResponse).toList());
    }


    // 게시글 응답 형변환
    private PostResponse getPostResponse(SharePost post, List<String> mediaUrls,
            List<Long> taggedUserIds) {
        return PostResponse.toResponse(
                post.getId(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                post.getCategory(),
                post.getManufactureDate(),
                post.getExpireDays(),
                post.getContent(),
                mediaUrls,
                taggedUserIds,
                post.isPublic(),
                post.getLikeCnt(),
                post.getWriterId()
        );
    }

    // 게시글 생성 및 수정 시 미디어 등록
    private List<String> saveAndGetMediaUrls(PostRequest request, SharePost targetPost) {

        // 기존 등록된 게시글 관련 미디어 전부 삭제
        mediaRepository.deleteAllByPostId(targetPost.getId());

        // request 에서 받아온 미디어 목록에 시퀀스 반영(오름차순), 순서 보장하여 저장, 리스트로 가공하여 반환
        List<Media> medias = IntStream.range(0, request.getMediaUrls().size())
                .mapToObj(i -> Media.builder()
                        .postId(targetPost.getId())
                        .mediaUrl(request.getMediaUrls().get(i))
                        .type(Type.SHARED)
                        .sequence(i)    // 순서 정보 부여
                        .build())
                .toList();

        List<Media> savedMedias = mediaRepository.saveAll(medias);

        return savedMedias.stream().map(Media::getMediaUrl).toList();
    }

    // 게시글 생성 및 수정 시 태그 등록
    private List<Long> saveAndGetTaggedUserIds(PostRequest request, SharePost targetPost) {

        // 사용자 태그를 20명으로 제한
        if (request.getTaggedUserIds().size() > TAGGED_USER_MAX_SIZE) {
            throw new PostInvalidException(ErrorType.TAGGED_USER_MAX_SIZE_20_ERROR);
        }

        // 기존 등록된 태그 전부 삭제
        taggedUserRepository.deleteAllByPostId(targetPost.getId());

        // request 에서 받아온 태그된 유저의 id 목록을 저장, 리스트로 가공하여 반환
        List<TaggedUser> taggedUsers = IntStream.range(0, request.getTaggedUserIds().size())
                .mapToObj(i -> TaggedUser.builder()
                        .postId(targetPost.getId())
                        .taggedUserId(request.getTaggedUserIds().get(i))
                        .sequence(i)
                        .build())
                .toList();

        List<TaggedUser> savedTaggedUsers = taggedUserRepository.saveAll(taggedUsers);

        return savedTaggedUsers.stream().map(TaggedUser::getTaggedUserId).toList();
    }

    // 게시글 연관 미디어 순서 보장하여 조회
    private List<String> getMediaUrls(SharePost post) {
        return mediaRepository.findALLByPostIdOrderBySequenceAsc(post.getId()).stream()
                .map(Media::getMediaUrl)
                .toList();
    }

    // 게시글 연관 태그 순서 보장하여 조회
    private List<Long> getTaggedUserIds(SharePost post) {
        return taggedUserRepository.findAllByPostIdOrderBySequenceAsc(post.getId()).stream()
                .map(TaggedUser::getTaggedUserId)
                .toList();
    }

    // 기존 추천한 이력 유무에 따른 추천수 증감
    private void updateLikeStatus(Optional<PostLike> postLike, SharePost findPost, User findUser) {
        if (postLike.isEmpty()) {
            postLikeRepository.save(PostLike.builder()
                    .likedAt(LocalDate.now())
                    .postId(findPost.getId())
                    .likerId(findUser.getId())
                    .build());
            findPost.increaseRecommendationCnt();
        } else {
            postLikeRepository.deleteById(postLike.get().getId());
            findPost.decreaseRecommendationCnt();
        }
    }

    // 게시글 정보 응답 dto 로 변환
    private PostInfoResponse convertToPostInfoResponse(SharePost post) {
        Media thumbnailMedia = mediaRepository.findALLByPostIdOrderBySequenceAsc(post.getId()).get(0);
        return PostInfoResponse.toResponse(post.getId(), post.getContent(),
                thumbnailMedia.getMediaUrl());
    }

    // 게시글 반환, 없으면 예외처리
    public SharePost getPostOrThrowIfNotExist(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR)
        );
    }
}
