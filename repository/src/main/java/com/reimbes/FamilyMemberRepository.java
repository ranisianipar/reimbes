package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
     FamilyMember findByName(String name);

     Page<FamilyMember> findByFamilyMemberOfAndNameContainingIgnoreCase(ReimsUser employee, String name, Pageable pageable);
     Page<FamilyMember> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
