package com.double_o.dambap.like.domain;

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
        name = "like",
        indexes = {
                @Index(name = "idx_like_targetid", columnList = "target_id"),
                @Index(name = "idx_like_likerid", columnList = "liker_id"),
                @Index(name = "uq_like_targetid_likerid", columnList = "target_id, liker_id", unique = true)
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "liked_at")
    private LocalDate likedAt;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "liker_id")
    private Long likerId;

    @Enumerated(EnumType.STRING)
    private Type type;
}
