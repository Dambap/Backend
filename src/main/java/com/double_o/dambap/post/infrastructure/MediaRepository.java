package com.double_o.dambap.post.infrastructure;

import com.double_o.dambap.post.domain.Media;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface MediaRepository extends JpaRepository<Media, Long> {

    void deleteAllByPostId(Long postId);

    List<Media> findALLByPostIdOrderBySequenceAsc(Long postId);
}
