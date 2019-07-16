package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimsUserRepository extends JpaRepository<ReimsUser, Long> {
    ReimsUser findByUsername(String username);
    Page<ReimsUser> findByUsernameContaining(String username, Pageable pageable);
    boolean existsByUsername(String username);
}
