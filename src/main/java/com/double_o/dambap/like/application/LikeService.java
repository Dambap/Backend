package com.double_o.dambap.like.application;

import com.double_o.dambap.like.common.Likeable;
import com.double_o.dambap.like.domain.Like;
import com.double_o.dambap.like.infrastructure.LikeRepository;
import com.double_o.dambap.post.domain.Type;
import com.double_o.dambap.user.domain.User;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    // 기존 추천한 이력 유무에 따른 추천수 증감
    public <T extends Likeable> void updateLikeStatus(T target, User findUser, Type type) {

        Optional<Like> like = likeRepository.findByTargetIdAndLikerId(
                target.getId(), findUser.getId());

        if (like.isEmpty()) {
            likeRepository.save(Like.builder()
                    .likedAt(LocalDate.now())
                    .targetId(target.getId())
                    .likerId(findUser.getId())
                    .type(type)
                    .build());
            target.increaseLikeCnt();
        } else {
            likeRepository.deleteById(like.get().getId());
            target.decreaseLikeCnt();
        }
    }

}
