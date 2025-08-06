package com.double_o.dambap.user.infrastructure;

import com.double_o.dambap.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
