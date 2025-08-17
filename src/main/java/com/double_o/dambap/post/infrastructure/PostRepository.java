package com.double_o.dambap.post.infrastructure;

import com.double_o.dambap.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findAllPostOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.writerId=:userId ORDER BY p.createdAt DESC")
    Page<Post> findAllMySharedPostOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
