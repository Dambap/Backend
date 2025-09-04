package com.double_o.dambap.recommendation.common;

public interface Recommendable {

    Long getId();

    void increaseRecommendationCnt();

    void decreaseRecommendationCnt();
}
