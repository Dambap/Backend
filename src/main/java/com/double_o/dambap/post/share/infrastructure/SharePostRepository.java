package com.double_o.dambap.post.share.infrastructure;

import com.double_o.dambap.post.share.domain.SharePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface SharePostRepository extends JpaRepository<SharePost, Long> {

    Page<SharePost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<SharePost> findAllByWriterIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
