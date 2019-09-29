package com.reimbes;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "Family_Member")
@Entity
@Data
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    @NotNull
    private String name;

    @NotNull
    private Relationship relationship;

    @NotNull
    private Date dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "employee", nullable = false)
    private ReimsUser employee;

    public enum Relationship {
        SPOUSE,
        CHILDREN
    }


}
