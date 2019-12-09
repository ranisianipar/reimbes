package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.FamilyMemberServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.reimbes.ReimsUser.Role.USER;
import static org.junit.Assert.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = FamilyMemberServiceImpl.class)
public class FamilyMemberServiceTest {


    @Mock
    private FamilyMemberRepository repository;

    @InjectMocks
    private FamilyMemberServiceImpl service;


    private ReimsUser maleUser;
    private ReimsUser femaleUser;

    private FamilyMember familyMember;

    private Set<FamilyMember> familyMembers;

    @Before
    public void setUp() throws ParseException {

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
                // .familyMemberOf(maleUser)
                .build();

        familyMembers = new HashSet<>();
        familyMembers.add(familyMember);

         maleUser.setFamilyMemberOf(familyMembers);

        // USER with gender FEMALE, cant register HER FAMILY.
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
        when(repository.save(familyMember)).thenReturn(familyMember);
        assertEquals(service.create(maleUser.getId(), familyMember), familyMember);
    }

    @Test
    public void failedRegisterFamilyMemberForFemaleUser() throws Exception {
        assertThrows(ReimsException.class, () -> service.create(femaleUser.getId(), familyMember));
    }

}
