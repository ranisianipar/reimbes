package com.reimbes;

import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MedicalService {

    Medical create(Medical medical, MultipartFile file) throws ReimsException;
    Medical update(long id, Medical newMedical, MultipartFile file) throws ReimsException;
    Medical get(long id) throws ReimsException;
    Page<Medical> getAll(Pageable pageRequest, String title, String startDate, String endDate);
    void delete(long id) throws ReimsException;

}
