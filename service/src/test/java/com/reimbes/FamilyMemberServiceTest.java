package com.reimbes;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.MethodNotAllowedException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.FamilyMemberServiceImpl;
import com.reimbes.interfaces.AuthService;
import com.reimbes.interfaces.FamilyMemberService;
import com.reimbes.interfaces.UserService;
import com.reimbes.interfaces.UtilsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.ReimsUser.Role.USER;
import static com.reimbes.constant.General.NULL_USER_ID_CODE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = FamilyMemberServiceImpl.class)
public class FamilyMemberServiceTest {

    @Mock
    private FamilyMemberRepository repository;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private UtilsService utilsService;

    @InjectMocks
    private FamilyMemberServiceImpl service;

    private ReimsUser admin;
    private ReimsUser maleUser;
    private ReimsUser femaleUser;

    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());

    private FamilyMember familyMember;
    private FamilyMember familyMember2;

    private Set<FamilyMember> familyMembers;

    @After
    public void tearDown() {
        verifyNoMoreInteractions(utilsService, userService, authService, repository);
    }

    @Before
    public void setUp() throws ParseException {

        admin = ReimsUser.ReimsUserBuilder()
                .username("admin")
                .password("adminhaha")
                .role(ReimsUser.Role.ADMIN)
                .id(1)
                .build();

        // USER with HIS FAMILY
        maleUser = ReimsUser.ReimsUserBuilder()
                .username("joker")
                .password("HEHE")
                .role(USER)
                .id(1)
                .gender(ReimsUser.Gender.MALE)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("13/05/1998"))
                .build();

        familyMember = FamilyMember.FamilyMemberBuilder()
                .name("Kehlani")
                .dateOfBirth(new Date())
                .relationship(FamilyMember.Relationship.SPOUSE)
                .familyMemberOf(maleUser)
                .id(maleUser.getId() + 1)
                .build();

        familyMember2 = FamilyMember.FamilyMemberBuilder()
                .name("Shawn Mendes")
                .dateOfBirth(new Date())
                .relationship(FamilyMember.Relationship.SPOUSE)
                .familyMemberOf(maleUser)
                .id(familyMember.getId() + 1)
                .build();

        familyMembers = new HashSet<>();
        familyMembers.add(familyMember);

         maleUser.setFamilyMemberOf(familyMembers);

        // USER with gender FEMALE
        femaleUser = ReimsUser.ReimsUserBuilder()
                .username("harley queen")
                .password("xixixi")
                .role(USER)
                .id(2)
                .gender(ReimsUser.Gender.FEMALE)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("13/05/1998"))
                .build();
    }

    @Test
    public void returnNullWhenCreateFamilyMemberForAdmin() throws Exception {
        when(userService.get(admin.getId())).thenReturn(admin);
        assertThrows(DataConstraintException.class, () -> {
                service.create(admin.getId(), familyMember);
        });
        verify(userService).get(admin.getId());
    }

    @Test
    public void throwError_whenAdminCreateFamilyMemberForUnexistUser() throws Exception {
        ReimsUser dummyUser = ReimsUser.ReimsUserBuilder().id(12414).build();
        when(userService.get(dummyUser.getId())).thenThrow(new NotFoundException(dummyUser.getId() + ""));
        assertThrows(NotFoundException.class, () -> service.create(dummyUser.getId(), familyMember));
        verify(userService).get(dummyUser.getId());
    }

    @Test
    public void succeedRegisterFamilyMemberForMaleUser() throws Exception {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));
        FamilyMember result;
        familyMember.setCreatedAt(now.toEpochMilli());

        when(userService.get(familyMember.getFamilyMemberOf().getId())).thenReturn(familyMember.getFamilyMemberOf());
        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());

        result = service.create(familyMember.getFamilyMemberOf().getId(), familyMember);

        verify(userService).get(familyMember.getFamilyMemberOf().getId());
        verify(repository).countByFamilyMemberOf(familyMember.getFamilyMemberOf());
        verify(repository).findByName(familyMember.getName());
        verify(utilsService).getCurrentTime();
        verify(repository).save(familyMember);

        assertNull(result);
    }

    @Test
    public void errorThrown_whenAdminCreateFamilyMemberWithInvalidData() throws ReimsException {
        when(userService.get(maleUser.getId())).thenReturn(maleUser);

        familyMember.setName(null);
        familyMember.setDateOfBirth(null);
        familyMember.setRelationship(null);

        assertThrows(DataConstraintException.class, () -> {
            service.create(maleUser.getId(), familyMember);
        });
        verify(userService).get(maleUser.getId());
        verify(repository).countByFamilyMemberOf(maleUser);
    }

    @Test
    public void errorThrown_whenAdminCreateFamilyMemberWithDuplicateFamilyMemberData() throws ReimsException {
        when(userService.get(maleUser.getId())).thenReturn(maleUser);
        familyMember2.setName(familyMember.getName());
        when(repository.findByName(familyMember.getName())).thenReturn(familyMember2);

        assertThrows(DataConstraintException.class, () -> {
            service.create(maleUser.getId(), familyMember);
        });
        verify(userService).get(maleUser.getId());
        verify(repository).findByName(familyMember.getName()); // validation
        verify(repository).countByFamilyMemberOf(maleUser);
    }

    @Test
    public void returnFamilyMember_whenAdminGetById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);

        FamilyMember result = service.getById(familyMember.getId());
        verify(authService).getCurrentUser();
        verify(repository).findOne(familyMember.getId());

        assertEquals(familyMember, result);
    }

    @Test
    public void returnFamilyMember_whenReimsUserOfFamilyMemberGetById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(maleUser);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);

        FamilyMember result = service.getById(familyMember.getId());
        verify(authService).getCurrentUser();
        verify(repository).findOne(familyMember.getId());

        assertEquals(familyMember, result);
    }

    @Test
    public void errorThrown_whenUnwantedUserGetFamilyMemberById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(femaleUser);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);

        assertThrows(NotFoundException.class, () -> {
            service.getById(familyMember.getId());
        });

        verify(authService).getCurrentUser();
        verify(repository).findOne(familyMember.getId());
    }

    @Test
    public void succeedUpdateFamilyMember() throws Exception {
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);
        when(repository.save(familyMember2)).thenReturn(familyMember2);
        when(userService.get(maleUser.getId())).thenReturn(maleUser);

        FamilyMember result = service.update(familyMember.getId(), familyMember2, maleUser.getId());
        verify(repository).findOne(familyMember.getId());
        verify(userService).get(maleUser.getId());
        verify(repository).findByName(familyMember.getName()); // validation
        verify(repository).save(familyMember2);

        assertEquals(familyMember2, result);

    }

    @Test
    public void errorThrown_whenAdminUpdateFamilyMemberAndAssignedMemberToAdmin() throws Exception {
        familyMember.setFamilyMemberOf(admin);

        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);
        when(userService.get(admin.getId())).thenReturn(admin);

        assertThrows(DataConstraintException.class, () -> {
            service.update(familyMember.getId(), familyMember2, admin.getId());
        });

        verify(repository).findOne(familyMember.getId());
        verify(userService).get(admin.getId());

    }

    @Test
    public void errorThrown_whenAdminUpdateFamilyMemberWithInvalidData() throws Exception {
        familyMember2.setName(familyMember.getName());

        when(repository.findOne(familyMember2.getId())).thenReturn(familyMember2);
        when(repository.findByName(familyMember2.getName())).thenReturn(familyMember);

        assertThrows(DataConstraintException.class, () -> {
            service.update(familyMember2.getId(), familyMember2, NULL_USER_ID_CODE); // update for himself
        });

        verify(repository).findOne(familyMember2.getId());
        verify(repository).findByName(familyMember2.getName());

    }


    @Test
    public void removeFamilyMember() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);
        assertThrows(MethodNotAllowedException.class, () -> {
            service.delete(familyMember.getId());
        });
        verify(authService).getCurrentUser();
        verify(repository).findOne(familyMember.getId());
    }

    @Test
    public void errorThrown_whenUserTryToRemoveFamilyMember() {
        familyMember.setFamilyMemberOf(maleUser);
        when(authService.getCurrentUser()).thenReturn(femaleUser);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);
        assertThrows(MethodNotAllowedException.class, () -> service.delete(familyMember.getId()));
        verify(authService).getCurrentUser();
        verify(repository).findOne(familyMember.getId());
    }

    @Test
    public void getAll_forUser_succeed() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);
        Page result;

        when(authService.getCurrentUser()).thenReturn(maleUser); // set current user
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(service.getAllByUser(maleUser, familyMember.getName(), pageForQuery)).thenReturn(page);

        result = service.getAll(null, familyMember.getName(), pageRequest);
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
        verify(repository).findByFamilyMemberOfAndNameContainingIgnoreCase(maleUser, familyMember.getName(), pageForQuery);

        assertEquals(page, result);
    }

    @Test
    public void getAll_ForAdmin_withUserQuery_succeed() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);
        Page result;

        when(authService.getCurrentUser()).thenReturn(admin); // set current user
        when(userService.get(maleUser.getId())).thenReturn(maleUser); // set current user
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(repository.findByFamilyMemberOfAndNameContainingIgnoreCase(maleUser, familyMember.getName(), pageForQuery)).thenReturn(page);

        result = service.getAll(maleUser.getId(), familyMember.getName(), pageRequest);
        verify(authService).getCurrentUser();
        verify(userService).get(maleUser.getId());
        verify(utilsService).getPageRequest(pageRequest);
        verify(repository).findByFamilyMemberOfAndNameContainingIgnoreCase(maleUser, familyMember.getName(), pageForQuery);
        assertEquals(page, result);
    }

    @Test
    public void getAll_ForAdmin_withoutUserQuery_succeed() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);
        Page result;

        when(authService.getCurrentUser()).thenReturn(admin); // set current user
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(repository.findByNameContainingIgnoreCase(familyMember.getName(), pageForQuery)).thenReturn(page);

        result = service.getAll(null, familyMember.getName(), pageRequest);
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
        verify(repository).findByNameContainingIgnoreCase(familyMember.getName(), pageForQuery);
        assertEquals(page, result);
    }


}
