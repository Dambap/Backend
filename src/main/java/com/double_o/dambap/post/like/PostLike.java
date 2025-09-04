package com.double_o.dambap.post.like;

import com.double_o.dambap.post.share.domain.Type;
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
//@Table(name = "post_like")
// 조회 성능을 위한 JPA 이용 인덱스 추가
@Table(
        name = "post_like",
        indexes = {
                @Index(name = "idx_postlike_postid", columnList = "post_id"),
                @Index(name = "idx_postlike_likerid", columnList = "liker_id"),
                @Index(name = "uq_postlike_postid_likerid", columnList = "post_id, liker_id", unique = true)
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "liked_at")
    private LocalDate likedAt;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "liker_id")
    private Long likerId;

    @Enumerated(EnumType.STRING)
    private Type type;
}
