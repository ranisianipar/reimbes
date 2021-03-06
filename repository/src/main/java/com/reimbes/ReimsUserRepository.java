package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimsUserRepository extends JpaRepository<ReimsUser, Long> {
    boolean existsByUsername(String username);
    ReimsUser findByUsername(String username);
    Page<ReimsUser> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<ReimsUser> findByIdGreaterThanAndUsernameContainingIgnoreCase(long superAdminId, String username, Pageable pageable);
}
