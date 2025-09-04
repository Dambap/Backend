package com.double_o.dambap.recommendation.domain;

import com.double_o.dambap.post.domain.Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
// 조회 성능을 위한 JPA 이용 인덱스 추가
@Table(
        name = "recommendation",
        indexes = {
                @Index(name = "idx_recommendation_targetid", columnList = "target_id"),
                @Index(name = "idx_recommendation_recommenderid", columnList = "recommender_id"),
                @Index(name = "uq_recommendation_targetid_recommenderid", columnList = "target_id, recommender_id", unique = true)
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recommended_at")
    private LocalDate recommendedAt;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "recommender_id")
    private Long recommenderId;

    @Enumerated(EnumType.STRING)
    private Type type;
}
