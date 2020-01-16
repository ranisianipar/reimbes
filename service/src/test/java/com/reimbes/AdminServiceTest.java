package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = AdminServiceImpl.class)
public class AdminServiceTest {

    @Mock
    private UtilsServiceImpl utilsServiceImpl;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private MedicalServiceImpl medicalService;

    @Mock
    private FamilyMemberServiceImpl familyMemberService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private ReimsUser user;
    private ReimsUser user2;
    private ReimsUser admin;
    private Medical medical;
    private FamilyMember familyMember;
    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());

    @Before
    public void setup() throws ReimsException {
        admin = ReimsUser.ReimsUserBuilder()
                .username("admin")
                .password("adminhaha")
                .role(ReimsUser.Role.ADMIN)
                .id(1)
                .build();

        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(2)
                .gender(ReimsUser.Gender.FEMALE)
                .dateOfBirth(new Date())
                .build();


        user2 = ReimsUser.ReimsUserBuilder()
                .username(user.getUsername()+"123")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(user.getId()+1)
                .gender(ReimsUser.Gender.MALE)
                .dateOfBirth(new Date())
                .build();

        medical = Medical.builder()
                .title("haha")
                .date((new Date()).getTime())
                .age(1)
                .amount(10000)
                .medicalUser(user)
                .build();

        familyMember = FamilyMember.FamilyMemberBuilder()
                .dateOfBirth(new Date())
                .id(user2.getId() + 1)
                .familyMemberOf(user)
                .relationship(FamilyMember.Relationship.SPOUSE)
                .name("HEHEHE")
                .build();

        when(authService.getCurrentUser()).thenReturn(admin);
        when(utilsServiceImpl.getPrincipalUsername()).thenReturn(admin.getUsername());
    }

    @Test
    public void returnAllUsers() throws ReimsException {
        List users = new ArrayList();
        users.add(user);
        Page page = new PageImpl(users);

        when(userService.getAllUsers(user.getUsername(), pageForQuery)).thenReturn(page);

        assertEquals(page, adminService.getAllUser(user.getUsername(), pageRequest));
    }

    @Test
    public void expectedError_whenPageRequestIndexIsZero() {
        assertThrows(ReimsException.class, () -> {
            adminService.getAllUser(user.getUsername(), pageForQuery);
        });
    }

    @Test
    public void returnUserById() throws ReimsException{
        when(userService.get(user.getId())).thenReturn(user);

        assertEquals(user, adminService.getUser(user.getId()));
    }


    @Test
    public void returnUser_whenAdminCreateUser() throws Exception{
        when(userService.create(user)).thenReturn(user);
        assertEquals(user, adminService.createUser(user));
    }

    @Test
    public void thrownError_whenAdminCreateUserWithInvalidData() {
        user.setGender(null);
        user.setDateOfBirth(null);
        assertThrows(ReimsException.class, () -> {
            adminService.createUser(user); // no gender and date of birth with role: USER
        });

        user.setRole(null);
        assertThrows(ReimsException.class, () -> {
            adminService.createUser(user); // no role assigned
        });
    }

    @Test
    public void returnUpdatedUser_whenAdminUpdateUser() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.update(user2.getId(), user2)).thenReturn(user2);

        assertEquals(user2, adminService.updateUser(user2.getId(), user2, null));
    }

    @Test
    public void returnUpdatedUser_whenUserUpdateHimself() throws ReimsException {

        ReimsUser userWithNewData = ReimsUser.ReimsUserBuilder()
                .username(admin.getUsername() + "HEHE")
                .role(admin.getRole())
                .id(admin.getId())
                .build();

        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userService.updateMyData(admin, response)).thenReturn(userWithNewData); // mock response

        assertEquals(userWithNewData, adminService.updateUser(admin.getId(), admin, response));
    }

    @Test
    public void doUserDeletion_whenAdminDeleteUserById() throws ReimsException {
        adminService.deleteUser(user2.getId());
        verify(userService, times(1)).delete(user2.getId());
    }

    @Test
    public void throwErrorForUserDeletion_whenAdminDeleteHimself() {
        assertThrows(ReimsException.class, () -> {
            adminService.deleteUser(admin.getId());
        });

    }

    /*
    *
    * MEDICAL
    *
    */

    @Test
    public void returnPageOfMedical_whenAdminGetAllMedicalWithCriteria() throws ReimsException {
        long start; long end;
        start = end = medical.getDate();

        List<Medical> medicals = new ArrayList<>();
        medicals.add(medical);
        Page<Medical> page = new PageImpl(medicals);
        when(medicalService.getAll(pageRequest, medical.getTitle(), start, end, user.getId())).thenReturn(page);
        assertEquals(adminService.getAllMedical(pageRequest, medical.getTitle(), start, end, user.getId()), page);
    }

    @Test
    public void returnAMedical_whenAdminGetMedicalById() throws ReimsException {
        when(medicalService.get(medical.getId())).thenReturn(medical);
        assertEquals(adminService.getMedical(medical.getId()), medical);
    }

    /*
     *
     * FAMILY MEMBER
     *
     */


    @Test
    public void returnPageOfFamileMember_whenAdminGetAllFamilyMemberWithCriteria() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page<FamilyMember> page = new PageImpl(members);
        when(familyMemberService.getAll(user.getId(), familyMember.getName(), pageRequest)).thenReturn(page);
        assertEquals(adminService.getAllFamilyMember(user.getId(), familyMember.getName(), pageRequest), page);
    }

    @Test
    public void returnAFamilyMember_whenAdminGetFamilyMemberById() throws ReimsException {
        when(familyMemberService.getById(familyMember.getId())).thenReturn(familyMember);
        assertEquals(adminService.getFamilyMember(familyMember.getId()), familyMember);
    }

    @Test
    public void returnFamilyMember_whenAdminCreateFamilyMemberOfAUser() throws ReimsException {
        when(familyMemberService.create(familyMember.getFamilyMemberOf().getId(), familyMember)).thenReturn(familyMember);
        assertEquals(adminService.createFamilyMember(familyMember.getFamilyMemberOf().getId(), familyMember), familyMember);
    }

    @Test
    public void returnFamilyMemberWithLatestData_whenAdminUpdateFamilyMemberById() throws ReimsException {
        familyMember.setFamilyMemberOf(user2);
        when(familyMemberService.update(familyMember.getId(), familyMember, user2.getId())).thenReturn(familyMember);
        assertEquals(adminService.updateFamilyMember(familyMember.getId(), familyMember, user2.getId()), familyMember);
    }

    @Test
    public void deleteFamilyMember_whenAdminDeleteFamilyMemberById() throws ReimsException {
        adminService.deleteFamilyMember(familyMember.getId());
        verify(familyMemberService, times(1)).delete(familyMember.getId());
    }









}
