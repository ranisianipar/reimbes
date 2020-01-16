package com.reimbes;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.MethodNotAllowedException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.implementation.FamilyMemberServiceImpl;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.implementation.UtilsServiceImpl;
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
    private AuthServiceImpl authService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UtilsServiceImpl utilsServiceImpl;

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
    public void succeedRegisterFamilyMemberForMaleUser() throws Exception {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        when(utilsServiceImpl.getCurrentTime()).thenReturn(now.toEpochMilli());
        when(userService.get(familyMember.getFamilyMemberOf().getId())).thenReturn(familyMember.getFamilyMemberOf());

        // Prohibit family member for ADMIN
        maleUser.setRole(ADMIN);
        assertNull(service.create(maleUser.getId(), familyMember));

        // createdAt is set in create() method
        maleUser.setRole(USER);
        familyMember.setCreatedAt(now.toEpochMilli());
        when(repository.save(familyMember)).thenReturn(familyMember);

        assertEquals(familyMember, service.create(maleUser.getId(), familyMember));
    }

    @Test
    public void errorThrown_whenAdminCreateFamilyMemberWithInvalidData() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(maleUser.getId())).thenReturn(maleUser);

        familyMember.setName(null);
        familyMember.setDateOfBirth(null);
        familyMember.setRelationship(null);

        assertThrows(DataConstraintException.class, () -> {
            service.create(maleUser.getId(), familyMember);
        });
    }

    @Test
    public void errorThrown_whenAdminCreateFamilyMemberWithDuplicateFamilyMemberData() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(maleUser.getId())).thenReturn(maleUser);
        familyMember2.setName(familyMember.getName());
        when(repository.findByName(familyMember.getName())).thenReturn(familyMember2);

        assertThrows(DataConstraintException.class, () -> {
            service.create(maleUser.getId(), familyMember);
        });
    }

    @Test
    public void returnFamilyMember_whenAdminGetById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);

        assertEquals(familyMember, service.getById(familyMember.getId()));
    }

    @Test
    public void returnFamilyMember_whenReimsUserOfFamilyMemberGetById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(maleUser);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);

        assertEquals(familyMember, service.getById(familyMember.getId()));
    }

    @Test
    public void errorThrown_whenUnwantedUserGetFamilyMemberById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(femaleUser);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);

        assertThrows(NotFoundException.class, () -> {
            service.getById(familyMember.getId());
        });
    }

    @Test
    public void succeedUpdateFamilyMember() throws Exception {
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);
        when(repository.save(familyMember2)).thenReturn(familyMember2);
        when(userService.get(maleUser.getId())).thenReturn(maleUser);

        assertEquals(familyMember2, service.update(familyMember.getId(), familyMember2, maleUser.getId()));
    }

    @Test
    public void errorThrown_whenAdminUpdateFamilyMemberAndAssignedMemberToAdmin() throws Exception {
        familyMember.setFamilyMemberOf(admin);
        when(repository.findOne(familyMember.getId())).thenReturn(familyMember);
        when(repository.save(familyMember2)).thenReturn(familyMember2);
        when(userService.get(admin.getId())).thenReturn(admin);

        assertThrows(DataConstraintException.class, () -> {
            service.update(familyMember.getId(), familyMember2, admin.getId());
        } );
    }

    @Test
    public void errorThrown_whenAdminUpdateFamilyMemberWithInvalidData() throws Exception {
        familyMember2.setName(familyMember.getName());

        when(repository.findOne(familyMember2.getId())).thenReturn(familyMember2);
        when(repository.findByName(familyMember2.getName())).thenReturn(familyMember);
        when(userService.get(maleUser.getId())).thenReturn(maleUser);

        assertThrows(DataConstraintException.class, () -> {
            service.update(familyMember2.getId(), familyMember2, new Long(0));
        } );

    }


    @Test
    public void removeFamilyMember() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        service.delete(familyMember.getId());
        verify(repository, times(1)).delete(familyMember.getId());
    }

    @Test
    public void errorThrown_whenUserTryToRemoveFamilyMember() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(maleUser);
        assertThrows(MethodNotAllowedException.class, () -> {
            service.delete(familyMember.getId());
        });
    }

    @Test
    public void getAllFamilyMember_forAdmin() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);

        service.getAll(maleUser.getId(), familyMember.getName(), pageRequest);
    }

    @Test
    public void getAllByUser_noQuery_succeed() {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);

        when(repository.findByNameContainingIgnoreCase(familyMember.getName(), pageRequest)).thenReturn(page);
        assertEquals(page, service.getAllByUser(null, familyMember.getName(), pageRequest));
    }

    @Test
    public void getAllByUser_withUserQuery_succeed() {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);

        when(repository.findByFamilyMemberOfAndNameContainingIgnoreCase(maleUser, familyMember.getName(), pageRequest))
                .thenReturn(page);
        assertEquals(page, service.getAllByUser(maleUser, familyMember.getName(), pageRequest));
    }

    @Test
    public void getAll_forUser_succeed() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);

        when(authService.getCurrentUser()).thenReturn(maleUser); // set current user
        when(service.getAllByUser(maleUser, familyMember.getName(), pageForQuery)).thenReturn(page);

        assertEquals(page, service.getAll(null, familyMember.getName(), pageRequest));


        when(repository.findByFamilyMemberOfAndNameContainingIgnoreCase(maleUser, familyMember.getName(), pageRequest))
                .thenReturn(page);
        assertEquals(page, service.getAllByUser(maleUser, familyMember.getName(), pageRequest));
    }

    @Test
    public void getAll_ForAdmin_withUserQuery_succeed() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);

        when(authService.getCurrentUser()).thenReturn(admin); // set current user
        when(userService.get(maleUser.getId())).thenReturn(maleUser); // set current user
        when(service.getAllByUser(maleUser, familyMember.getName(), pageForQuery)).thenReturn(page);

        assertEquals(page, service.getAll(maleUser.getId(), familyMember.getName(), pageRequest));
    }

    @Test
    public void getAll_ForAdmin_withoutUserQuery_succeed() throws ReimsException {
        List<FamilyMember> members = new ArrayList<>();
        members.add(familyMember);
        Page page = new PageImpl(members);

        when(authService.getCurrentUser()).thenReturn(admin); // set current user
        when(service.getAllByUser(null, familyMember.getName(), pageForQuery)).thenReturn(page);

        assertEquals(page, service.getAll(null, familyMember.getName(), pageRequest));
    }


}
