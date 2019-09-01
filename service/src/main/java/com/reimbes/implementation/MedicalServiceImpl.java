package com.reimbes.implementation;

import com.reimbes.Medical;
import com.reimbes.MedicalRepository;
import com.reimbes.MedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MedicalServiceImpl implements MedicalService {


    @Autowired
    private MedicalRepository medicalRepository;


    @Override
    public Medical create(Medical medical) {
        return null;
    }

    @Override
    public Medical update(Medical newMedical) {
        return null;
    }

    @Override
    public Medical get(long id) {
        return null;
    }

    @Override
    public Page<Medical> getAll(PageRequest pageRequest, String title, long startDate, long endDate) {
        return null;
    }

    @Override
    public void delete(long id) {

    }
}
