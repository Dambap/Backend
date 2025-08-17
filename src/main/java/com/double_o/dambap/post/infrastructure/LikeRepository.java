package com.double_o.dambap.post.infrastructure;

import com.double_o.dambap.post.domain.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostIdAndLikerId(Long postId, Long likerId);
}
