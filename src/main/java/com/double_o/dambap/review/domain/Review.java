package com.double_o.dambap.review.domain;

import com.double_o.dambap.common.entity.BaseEntity;
import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.review.ReviewInvalidException;
import com.double_o.dambap.recommendation.common.Recommendable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Review extends BaseEntity implements Recommendable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 콘텐츠 정보
    private String content;
    @Column(name = "is_public")
    private boolean isPublic;
    @Column(name = "received_at")
    private LocalDate receivedAt;
    @Column(name = "is_finished")
    private boolean isFinished;

    // cnt 정보
    @Column(name = "recommendation_cnt", nullable = false)
    private int recommendationCnt = 0;

    // 참조 정보
    @Column(name = "writer_id")
    private Long writerId;
    @Column(name = "origin_post_id")
    private Long originPostId;


    public void update(
            String content,
            boolean isPublic,
            LocalDate receivedAt,
            boolean isFinished
    ) {
        this.content = content;
        this.isPublic = isPublic;
        this.receivedAt = receivedAt;
        this.isFinished = isFinished;
    }

    @Override
    public void increaseRecommendationCnt() {
        this.recommendationCnt++;
    }

    @Override
    public void decreaseRecommendationCnt() {
        if (this.recommendationCnt > 0) {
            this.recommendationCnt--;
        } else {
            throw new ReviewInvalidException(ErrorType.CNT_NEGATIVE_ERROR);
        }
    }

    public void changePublicity() {
        this.isPublic = !this.isPublic;
    }

    public void changeFinishedStatus() {
        this.isFinished = !this.isFinished;
    }
}
