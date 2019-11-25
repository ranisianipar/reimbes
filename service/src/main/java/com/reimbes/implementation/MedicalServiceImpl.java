package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.reimbes.implementation.Utils.countAge;

@Service
public class MedicalServiceImpl implements MedicalService {

    private static Logger log = LoggerFactory.getLogger(MedicalServiceImpl.class);


    @Autowired
    private MedicalRepository medicalRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private Utils utils;

//    MULTIPLE upload
    public Medical create(Medical medical, List<String> files) throws ReimsException {



        ReimsUser currentUser = authService.getCurrentUser();

        validate(medical);

        if (files != null) {
            Set<MedicalReport> reports = new HashSet<>();
            log.info("Create register all medical reports those have been attached.");
            for (String file: files) {
                reports.add(
                        MedicalReport.builder()
                                .image(utils.uploadImage(file, currentUser.getId(), UrlConstants.SUB_FOLDER_REPORT))
                                .build()
                );
            }
            medical.setReports(reports);
        }


        // [CHECK]: user claim medical for himself or not
        // patient null --> claim for himself
         medical.setMedicalUser(currentUser);
//        if (medical.getPatient() == null)
        medical.setAge(countAge(currentUser.getDateOfBirth()));

        log.info("MEDICAL --> "+medical.toString());
        return medicalRepository.save(medical);
    }

//    this method doesnt support medical report editing
    @Override
    public Medical update(long id, Medical newMedical, List<String> files) throws ReimsException {
        Medical old = medicalRepository.findOne(id);
        ReimsUser currentUser = authService.getCurrentUser();
        Set<MedicalReport> reports = new HashSet<>();

        validate(newMedical);
        old.setAmount(newMedical.getAmount());
//        old.setPatient(newMedical.getPatient());
//        old.setAge(countAge(newMedical.getPatient().getDateOfBirth()));
        old.setDate(newMedical.getDate());

        for (String file: files) {
            reports.add(
                    MedicalReport.builder()
                            .image(utils.uploadImage(file, currentUser.getId(), UrlConstants.SUB_FOLDER_REPORT))
                            .build()
            );
        }
        old.setReports(reports);

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

    private void validate(Medical report) throws DataConstraintException {
        ArrayList<String> errors = new ArrayList();

        if (report.getAmount() <= 0) errors.add("PROHIBITED_AMOUNT");
//        if (report.getPatient() == null) errors.add("NULL_ATTRIBUTE_PATIENT");

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());
    }
}
