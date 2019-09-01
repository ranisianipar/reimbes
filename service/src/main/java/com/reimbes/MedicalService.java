package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MedicalService {

    Medical create(Medical medical);
    Medical update(Medical newMedical);
    Medical get(long id);
    Page<Medical> getAll(PageRequest pageRequest, String title, long startDate, long endDate);
    void delete(long id);

}
