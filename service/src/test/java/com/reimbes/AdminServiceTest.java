package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AdminServiceImpl;
import com.reimbes.interfaces.*;
import org.junit.After;
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
    private UtilsService utilsService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private MedicalService medicalService;

    @Mock
    private FamilyMemberService familyMemberService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private ReimsUser user;
    private ReimsUser user2;
    private ReimsUser admin;
    private Medical medical;
    private FamilyMember familyMember;
    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());

    @After
    public void tearDown() {
        verifyNoMoreInteractions(utilsService, userService, medicalService, familyMemberService);
    }

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
                .username(user.getUsername() + "123")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(user.getId() + 1)
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
        when(utilsService.getPrincipalUsername()).thenReturn(admin.getUsername());
    }

    @Test
    public void returnAllUsers() throws ReimsException {
        List users = new ArrayList();
        users.add(user);
        Page expectedResult = new PageImpl(users);
        when(userService.getAllUsers(user.getUsername(), pageForQuery)).thenReturn(expectedResult);

        Page result = adminService.getAllUser(user.getUsername(), pageRequest);

        assertEquals(expectedResult, result);
        verify(userService).getAllUsers(user.getUsername(), pageForQuery);
    }

    @Test(expected = ReimsException.class)
    public void expectedError_whenPageRequestIndexIsZero() throws ReimsException {
        adminService.getAllUser(user.getUsername(), pageForQuery);
    }

    @Test
    public void returnUserById() throws ReimsException {
        when(userService.get(user.getId())).thenReturn(user);

        ReimsUser result = adminService.getUser(user.getId());
        assertEquals(user, result);
        verify(userService).get(user.getId());
    }


    @Test
    public void returnUser_whenAdminCreateUser() throws Exception {
        when(userService.create(user)).thenReturn(user);
        ReimsUser result = adminService.createUser(user);
        assertEquals(user, result);
        verify(userService).create(user);
    }

    @Test(expected = ReimsException.class)
    public void thrownError_whenAdminCreateUserWithInvalidData() throws ReimsException{
        user.setGender(null);
        user.setDateOfBirth(null);
        adminService.createUser(user); // no gender and date of birth with role: USER
    }

    @Test(expected = ReimsException.class)
    public void thrownError_whenAdminCreateUserWithNullRole() throws ReimsException {
        user.setGender(null);
        user.setDateOfBirth(null);
        user.setRole(null);

        adminService.createUser(user); // no role assigned
    }

    @Test
    public void returnUpdatedUser_whenAdminUpdateUser() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.update(user2.getId(), user2)).thenReturn(user2);

        ReimsUser result = adminService.updateUser(user2.getId(), user2, null);
        verify(userService).update(user2.getId(), user2);

        assertEquals(user2, result);
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

        ReimsUser result = adminService.updateUser(admin.getId(), admin, response);

        verify(userService).updateMyData(userWithNewData, response);
        assertEquals(userWithNewData, result);
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
     * MEDICAL_VALUE
     *
     */

    @Test
    public void returnPageOfMedical_whenAdminGetAllMedicalWithCriteria() throws ReimsException {
        long start;
        long end;
        start = end = medical.getDate();

        List<Medical> medicals = new ArrayList<>();
        medicals.add(medical);
        Page<Medical> expectedResult = new PageImpl(medicals);
        when(medicalService.getAll(pageRequest, medical.getTitle(), start, end, user.getId())).thenReturn(expectedResult);

        Page result = adminService.getAllMedical(pageRequest, medical.getTitle(), start, end, user.getId());
        verify(medicalService).getAll(pageRequest, medical.getTitle(), start, end, user.getId());

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnAMedical_whenAdminGetMedicalById() throws ReimsException {
        when(medicalService.get(medical.getId())).thenReturn(medical);

        Medical result = adminService.getMedical(medical.getId());
        verify(medicalService).get(medical.getId());
        assertEquals(medical, result);
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
        Page<FamilyMember> expectedResult = new PageImpl(members);
        when(familyMemberService.getAll(user.getId(), familyMember.getName(), pageRequest)).thenReturn(expectedResult);

        Page result = adminService.getAllFamilyMember(user.getId(), familyMember.getName(), pageRequest);
        verify(familyMemberService).getAll(user.getId(), familyMember.getName(), pageRequest);
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnAFamilyMember_whenAdminGetFamilyMemberById() throws ReimsException {
        when(familyMemberService.getById(familyMember.getId())).thenReturn(familyMember);

        FamilyMember result = adminService.getFamilyMember(familyMember.getId());
        verify(familyMemberService).getById(familyMember.getId());
        assertEquals(familyMember, result);
    }

    @Test
    public void returnFamilyMember_whenAdminCreateFamilyMemberOfAUser() throws ReimsException {
        when(familyMemberService.create(familyMember.getFamilyMemberOf().getId(), familyMember)).thenReturn(familyMember);

        FamilyMember result = adminService.createFamilyMember(familyMember.getFamilyMemberOf().getId(), familyMember);

        verify(familyMemberService).create(familyMember.getFamilyMemberOf().getId(), familyMember);
        assertEquals(familyMember, result);
    }

    @Test
    public void returnFamilyMemberWithLatestData_whenAdminUpdateFamilyMemberById() throws ReimsException {
        familyMember.setFamilyMemberOf(user2);
        when(familyMemberService.update(familyMember.getId(), familyMember, user2.getId())).thenReturn(familyMember);

        FamilyMember result = adminService.updateFamilyMember(familyMember.getId(), familyMember, user2.getId());
        verify(familyMemberService).update(familyMember.getId(), familyMember, user2.getId());
        assertEquals(familyMember, result);
    }

    @Test
    public void deleteFamilyMember_whenAdminDeleteFamilyMemberById() throws ReimsException {
        adminService.deleteFamilyMember(familyMember.getId());
        verify(familyMemberService, times(1)).delete(familyMember.getId());
    }


}
