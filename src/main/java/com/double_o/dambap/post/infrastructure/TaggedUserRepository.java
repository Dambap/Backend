package com.double_o.dambap.post.infrastructure;

import com.double_o.dambap.post.domain.TaggedUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TaggedUserRepository extends JpaRepository<TaggedUser, Long> {

    void deleteAllByPostId(Long postId);

    List<TaggedUser> findAllByPostIdOrderBySequenceAsc(Long postId);
}
