package com.reimbes;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.build.ToStringPlugin;

import javax.persistence.*;
import java.util.Set;

@Table(name="Medical")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Medical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    private String title;
    private long age; // current age, result of year of birth - current year
    private long amount;
    private long date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reimsUser", nullable = false)
    @JsonIgnore
    private ReimsUser medicalUser;

//    Mapped to multiple images
    @OneToMany(mappedBy = "medical_id")
    private Set<MedicalReport> attachments;


}
