package com.reimbes.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.reimbes.FamilyMember;
import lombok.Data;

import javax.management.relation.Relation;
import java.util.Date;
import java.util.List;

@Data
public class FamilyMemberResponse {

    private long id;

    private String name;

    // ID of Employee (ReimsUser)
    private long familyMemberOf;

    private FamilyMember.Relationship relationship;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfBirth;



}
