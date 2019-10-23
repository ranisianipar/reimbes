package com.reimbes;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Table(name = "Family_Member")
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    @JoinColumn(name = "familyMemberOf", nullable = false)
    private ReimsUser familyMemberOf;

    @OneToMany(mappedBy = "patient")
    private Set<Medical> medicals;


    public enum Relationship {
        SPOUSE,
        CHILDREN
    }


}
