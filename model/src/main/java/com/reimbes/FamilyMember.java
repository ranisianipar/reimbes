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


@Table(name = "Family_Member")
@Entity
@Data @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class FamilyMember extends Patient {

    @NotNull
    private Relationship relationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyMemberOf", nullable = false)
    @ToStringPlugin.Exclude
    @JsonIgnore
    private ReimsUser familyMemberOf;

    @Column(updatable = false)
    private long createdAt;


    public enum Relationship {
        SPOUSE,
        CHILDREN
    }

    @Builder(builderMethodName = "FamilyMemberBuilder")
    public FamilyMember(long id, ReimsUser familyMemberOf, Relationship relationship, String name, Date dateOfBirth,
                        long createdAt, long updatedAt) {
        super(id, name, dateOfBirth, createdAt, updatedAt);
        this.familyMemberOf = familyMemberOf;
        this.relationship = relationship;
    }

}
