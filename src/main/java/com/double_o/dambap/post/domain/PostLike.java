package com.double_o.dambap.post.domain;

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
@Table(name = "post_like")
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
}
