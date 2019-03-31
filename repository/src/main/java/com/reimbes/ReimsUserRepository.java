package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimsUserRepository extends JpaRepository<ReimsUser, Long> {
    public ReimsUser findByUsername(String username);
}
