package com.double_o.dambap.recommendation.application;

import com.double_o.dambap.recommendation.common.Recommendable;
import com.double_o.dambap.recommendation.domain.Recommendation;
import com.double_o.dambap.recommendation.infrastructure.RecommendRepository;
import com.double_o.dambap.post.domain.Type;
import com.double_o.dambap.user.domain.User;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;

    // 기존 추천한 이력 유무에 따른 추천수 증감
    public <T extends Recommendable> void updateRecommendStatus(T target, User findUser, Type type) {

        Optional<Recommendation> recommendation = recommendRepository.findByTargetIdAndRecommenderId(
                target.getId(), findUser.getId());

        if (recommendation.isPresent()) {
            recommendRepository.deleteById(recommendation.get().getId());
            target.decreaseRecommendationCnt();
        } else {
            recommendRepository.save(Recommendation.builder()
                    .recommendedAt(LocalDate.now())
                    .targetId(target.getId())
                    .recommenderId(findUser.getId())
                    .type(type)
                    .build());
            target.increaseRecommendationCnt();
        }
    }

}
