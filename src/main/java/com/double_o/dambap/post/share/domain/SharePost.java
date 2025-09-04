package com.double_o.dambap.post.share.domain;

import com.double_o.dambap.common.entity.BaseEntity;
import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.post.PostInvalidException;
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
@Table(name = "share_post")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SharePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 콘텐츠 정보
    private Category category;
    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;
    @Column(name = "expire_days")
    private int expireDays;
    private String content;
    @Column(name = "is_public")
    private boolean isPublic;

    // cnt 정보
    @Column(name = "like_cnt")
    private int likeCnt;

    // 참조 정보
    @Column(name = "writer_id")
    private Long writerId;

    public void updateSharePost(
            Category category,
            LocalDate manufactureDate,
            int expireDays,
            String content,
            boolean isPublic) {
        this.category = category;
        this.manufactureDate = manufactureDate;
        this.expireDays = expireDays;
        this.content = content;
        this.isPublic = isPublic;
    }

    public void increaseRecommendationCnt() {
        this.likeCnt++;
    }

    public void decreaseRecommendationCnt() {
        if (this.likeCnt > 0) {
            this.likeCnt--;
        } else {
            throw new PostInvalidException(ErrorType.CNT_NEGATIVE_ERROR);
        }
    }

    public void changePublicity() {
        this.isPublic = !this.isPublic;
    }
}
