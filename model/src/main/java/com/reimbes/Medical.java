package com.reimbes;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import net.bytebuddy.build.ToStringPlugin;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Table(name="Medical")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@ToString(onlyExplicitlyIncluded = true)
public class Medical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    @NonNull
    private String title;
    private long age; // current age, result of year of birth - current year
    private long amount;
    private long date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reimsUser", nullable = false)
    @JsonIgnore
    private ReimsUser medicalUser;

    //    Mapped to multiple images
    @OneToMany(mappedBy = "medicalImage", cascade = CascadeType.PERSIST)
    private Set<MedicalReport> attachments;

    @Column(updatable = false)
    private long createdAt;
}
