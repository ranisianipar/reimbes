package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.constant.General.DEFAULT_LONG_VALUE;
import static com.reimbes.constant.UrlConstants.SUB_FOLDER_REPORT;
import static com.reimbes.interfaces.UtilsService.countAge;

@Service
public class MedicalServiceImpl implements MedicalService {

    private static Logger log = LoggerFactory.getLogger(MedicalServiceImpl.class);


    @Autowired
    private MedicalRepository medicalRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private UtilsService utilsService;

    //    MULTIPLE upload
    @Override
    public Medical create(Medical medical, List<String> files) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();

        log.info("Create method called. With files: " + files);

        validate(medical);

        if (files != null) {
            Set<MedicalReport> reports = new HashSet<>();
            log.info("Create register all medical attachments those have been attached.");
            for (String file : files) {
                reports.add(
                        MedicalReport.builder()
                                .image(utilsService.uploadImage(file, currentUser.getId(), SUB_FOLDER_REPORT))
                                .medicalImage(medical)
                                .build()
                );
            }
            medical.setAttachments(reports);
        }

        Patient patient = (medical.getPatient() == null || medical.getPatient().getId() == 0)
                ? currentUser : familyMemberService.getById(medical.getPatient().getId());

        medical.setMedicalUser(currentUser);
        medical.setPatient(patient);
        medical.setAge(countAge(patient.getDateOfBirth()));
        medical.setCreatedAt(utilsService.getCurrentTime());

        return medicalRepository.save(medical);
    }

    /*
     * User cant update medical attachment
     */
    @Override
    public Medical update(long id, Medical newMedical, List<String> files) throws ReimsException {
        Medical old = medicalRepository.findOne(id);
        ReimsUser currentUser = authService.getCurrentUser();

        validate(newMedical);
        old.setAmount(newMedical.getAmount());

        Patient patient = (newMedical.getPatient() == null || newMedical.getPatient().getId() == currentUser.getId())
                ? currentUser : familyMemberService.getById(newMedical.getPatient().getId());

        old.setPatient(patient);
        old.setAge(countAge(patient.getDateOfBirth()));
        old.setDate(newMedical.getDate());

        return medicalRepository.save(old);
    }

    @Override
    public Medical get(long id) throws ReimsException {
        Medical report = medicalRepository.findOne(id);
        ReimsUser currentUser = authService.getCurrentUser();
        if (report == null || (report.getMedicalUser() != currentUser && currentUser.getRole() != ADMIN))
            throw new NotFoundException("MEDICAL_REPORT");
        return report;
    }

    @Override
    public Page<Medical> getAll(Pageable pageRequest, String title, Long start, Long end, Long userId) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();

        // enabling query by specific for admin. In the other hand, user get his medical report list
        ReimsUser queryUser;
        if (userId != 0 && currentUser.getRole() == ADMIN) {
            log.info("GET Medical with User ID criteria by ADMIN");
            queryUser = userService.get(userId);
        } else if (currentUser.getRole() == ADMIN) {
            log.info("GET Medical by ADMIN");
            queryUser = null;
        } else {
            log.info("GET Medical by User");
            queryUser = currentUser;
        }

        int index = pageRequest.getPageNumber() - 1;
        if (index < 0) index = 0;
        Pageable page = new PageRequest(index, pageRequest.getPageSize(), pageRequest.getSort());

        if (start == 0 && end == 0) {
            if (queryUser == null) return medicalRepository.findByTitleContainingIgnoreCase(title, page);
            log.info(String.format("GET Medical by title: %s; user: %d;", title, queryUser.getId()));
            return medicalRepository.findByTitleContainingIgnoreCaseAndMedicalUser(title, queryUser, page);
        } else {
            if (queryUser == null) {
                log.info(String.format("GET Medical by title: %s; start: %s; end: %s", title, start, end));
                return medicalRepository.findByTitleContainingIgnoreCaseAndDateBetween(title, start, end, page);
            }
            log.info(String.format("GET Medical by title: %s; start: %s; end: %s, user: %d", title, start, end, queryUser.getId()));
            return medicalRepository.findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(title, start, end, queryUser, page);
        }
    }

    @Override
    public List<Medical> getByDate(Long start, Long end) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();
        if (start == DEFAULT_LONG_VALUE && end == DEFAULT_LONG_VALUE)
            return medicalRepository.findByMedicalUser(user);
        return medicalRepository.findByDateBetweenAndMedicalUser(start, end, user);
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
        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());
    }
}
