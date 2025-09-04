package com.double_o.dambap.review.infrastructure;

import com.double_o.dambap.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByWriterIdOrderByCreatedAtDesc(Long writerId, Pageable pageable);

    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
