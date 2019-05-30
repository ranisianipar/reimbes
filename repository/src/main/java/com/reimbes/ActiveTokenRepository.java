package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveTokenRepository  extends JpaRepository<ActiveToken, String> {
    boolean existsByToken(String token);
    ActiveToken findByToken(String token);
}
