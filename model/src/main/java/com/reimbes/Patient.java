package com.reimbes;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Table(name = "Patient")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "patient_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    private String name;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date dateOfBirth;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Medical> medicals;

    @Column(updatable = false)
    private long createdAt;

    @Column
    private long updatedAt;


    @Builder(builderMethodName = "PatientBuilder")
    public Patient(long id, String name, Date dateOfBirth, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
