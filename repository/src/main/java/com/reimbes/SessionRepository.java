package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteByToken(String token);
    boolean existsByToken(String token);
    Session findByToken(String token);
}
