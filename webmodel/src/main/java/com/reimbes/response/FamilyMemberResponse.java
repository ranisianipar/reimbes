package com.reimbes.response;

import com.reimbes.FamilyMember;
import lombok.Data;

import javax.management.relation.Relation;
import java.util.List;

@Data
public class FamilyMemberResponse {

    private long id;

    private String name;

    // ID of Employee (ReimsUser)
    private long familyMemberOf;

    private FamilyMember.Relationship relationship;

    // epoch
    private long dateOfBirth;



}
