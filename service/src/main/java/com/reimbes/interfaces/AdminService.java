package com.reimbes.interfaces;

import com.reimbes.FamilyMember;
import com.reimbes.Medical;
import com.reimbes.ReimsUser;
import com.reimbes.Session;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;

public interface AdminService {
    Page getAllUser(String search, Pageable pageable) throws ReimsException;
    ReimsUser getUser(long id) throws ReimsException;
    ReimsUser createUser(ReimsUser user) throws ReimsException;
    boolean changePassword(String password) throws NotFoundException;

    //  ReimsUser / Login User
    Object updateUser(long id, ReimsUser user, String token) throws ReimsException;
    void deleteUser(long id) throws ReimsException;

    // Family Member

    // UserId: Query Family Member by
    Page<FamilyMember> getAllFamilyMember(Long userId, String familyMemberName, Pageable pageRequest) throws ReimsException;
    FamilyMember getFamilyMember(long memberId) throws ReimsException;
    FamilyMember createFamilyMember(long userId, FamilyMember member) throws ReimsException;
    void deleteFamilyMember(long familyMemberId) throws ReimsException;
    FamilyMember updateFamilyMember(long memberId, FamilyMember memberLatestData, long userId) throws ReimsException;

    // Medical

    Page<Medical> getAllMedical(Pageable page, String title, Long start, Long end, Long userId) throws ReimsException;
    Medical getMedical(long id) throws ReimsException;

}
