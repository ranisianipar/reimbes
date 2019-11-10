package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalServiceImpl implements MedicalService {


    @Autowired
    private MedicalRepository medicalRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private Utils utils;


    @Override
    public Medical create(Medical medical, MultipartFile file) throws ReimsException {

        ReimsUser currentUser = authService.getCurrentUser();

        validate(medical);
        if (file != null) medical.setAttachement(uploadFile(file));

        // check user claim medical for himself or not
        medical.setMedicalUser(currentUser);
        return medicalRepository.save(medical);
    }

    @Override
    public Medical update(long id, Medical newMedical, MultipartFile file) throws ReimsException {
        Medical old = medicalRepository.findOne(id);

        validate(newMedical);
        old.setAmount(newMedical.getAmount());
        old.setDateOfBirth(newMedical.getDateOfBirth());
        old.setPatient(newMedical.getPatient());
        old.setDate(newMedical.getDate());

        if (file != null) old.setAttachement(uploadFile(file));

        return medicalRepository.save(old);
    }

    @Override
    public Medical get(long id) throws ReimsException {
        Medical report = medicalRepository.findOne(id);
        if (report == null || report.getMedicalUser() != authService.getCurrentUser())
            throw new NotFoundException("MEDICAL_REPORT");
        return report;
    }

    @Override
    public Page<Medical> getAll(Pageable page, String title, String startDate, String endDate) {
        Long start; Long end;

        int index = page.getPageNumber() - 1;
        if (index < 0) index = 0;
        Pageable pageRequest = new PageRequest(index, page.getPageSize(), page.getSort());

        try {
            start = new Long(startDate);
            end = new Long(endDate);

            return medicalRepository.findByTitleContainingIgnoreCaseAndDateBetween(title, start, end, pageRequest);
        } catch (Exception e) {
            return medicalRepository.findByTitleContainingIgnoreCase(title, pageRequest);
        }
    }

    @Override
    public void delete(long id) throws ReimsException {
        Medical report = medicalRepository.findOne(id);
        if (report == null || report.getMedicalUser() != authService.getCurrentUser())
            throw new NotFoundException("MEDICAL_REPORT");

        medicalRepository.delete(id);
    }

    @Transactional
    public List<String> uploadFiles(List<MultipartFile> files) throws FormatTypeError {
        ReimsUser currentUser = authService.getCurrentUser();
        List<MedicalReport> reports = new ArrayList<>();

        // get filepath
        // create medical reports when each report upload succeed

        String filePath;
        for (MultipartFile file: files) {
            filePath = uploadFile(file);
            
        }



        // return list of file path string

        return null;
    }

    private String uploadFile(MultipartFile file) throws FormatTypeError {
        ReimsUser currentUser = authService.getCurrentUser();

        String filePath = utils.getFilePath(currentUser.getUsername(), file);
        try {
            utils.createFile(filePath, file.getBytes());
        }  catch (IOException e) {
            throw new FormatTypeError(e.getMessage());
        }
        return filePath;
    }

    private void validate(Medical report) throws DataConstraintException {
        ArrayList<String> errors = new ArrayList();

        if (report.getAmount() <= 0) errors.add("PROHIBITED_AMOUNT");
//        if (report.getPatient() == null) errors.add("NULL_ATTRIBUTE_PATIENT");

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());
    }
}
