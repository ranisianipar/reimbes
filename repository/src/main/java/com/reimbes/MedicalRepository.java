package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRepository extends JpaRepository<Medical, Long> {

    Page<Medical> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Medical> findByTitleContainingIgnoreCaseAndDateBetween(String title, long start, long end, Pageable pageable);
    Page<Medical> findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(String title, long start, long end, Patient patient, Pageable pageable);
    Page<Medical> findByTitleContainingIgnoreCaseAndPatient(String title, long start, long end, Patient patient, Pageable pageable);
    Page<Medical> findByMedicalUser(ReimsUser medicalUser, Pageable pageable);

}
