package com.reimbes.interfaces;

import com.reimbes.Medical;
import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MedicalService {

    Medical create(Medical medical, List<String> files) throws ReimsException;
    Medical update(long id, Medical newMedical, List<String> files) throws ReimsException;
    Medical get(long id) throws ReimsException;
    Page<Medical> getAll(Pageable pageRequest, String title, Long startDate, Long endDate, Long userId) throws ReimsException;
    void delete(long id) throws ReimsException;
    List<Medical> getByDate(Long start, Long end) throws ReimsException;

}
