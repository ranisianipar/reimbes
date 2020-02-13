package com.reimbes.interfaces;

import com.reimbes.FamilyMember;
import com.reimbes.ReimsUser;
import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FamilyMemberService {
    FamilyMember create(Long userId, FamilyMember member) throws ReimsException;
    FamilyMember getById(Long id) throws ReimsException;
    Page getAll(Long userId, String nameFilter, Pageable pageRequest) throws ReimsException;
    Page getAllByUser(ReimsUser searchUser, String name, Pageable pageable);
    void delete(long id) throws ReimsException;
    FamilyMember update(long id, FamilyMember latestData, long userId) throws ReimsException;

}
