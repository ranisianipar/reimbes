package com.reimbes;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.build.ToStringPlugin;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Table(name = "Family_Member")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    private Date dateOfBirth; // yyyy-MM-dd

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyMemberOf", nullable = false)
    @ToStringPlugin.Exclude private ReimsUser familyMemberOf;

//    @OneToMany(mappedBy = "patient")
//    private Set<Medical> medicals;


    public enum Relationship {
        SPOUSE,
        CHILDREN
    }



}
