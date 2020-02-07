package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteByToken(String token);
    boolean existsByToken(String token);
    boolean existsByUsername(String username);
    Session findByToken(String token);
    Session findByUsername(String username);
}
