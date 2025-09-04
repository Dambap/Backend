package com.double_o.dambap.media;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface MediaRepository extends JpaRepository<Media, Long> {

    void deleteAllByTargetId(Long postId);

    void deleteByTargetId(Long targetId);

    List<Media> findALLByTargetIdOrderBySequenceAsc(Long postId);

    Media findByTargetId(Long postId);
}
