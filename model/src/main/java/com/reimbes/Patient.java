package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Table(name = "Patient")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name="patient_type", discriminatorType = DiscriminatorType.STRING)

@Data @NoArgsConstructor

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    private String name;

    // null for ADMIN
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @OneToMany(mappedBy = "patient", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Medical> medicals;


    @Builder(builderMethodName = "PatientBuilder")
    public Patient(long id, String name, Date dateOfBirth) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }
}
