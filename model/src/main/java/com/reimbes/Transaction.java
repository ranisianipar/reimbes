package com.reimbes;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "transaction_id")
    private long id;

    private String title;

    private long amount;
    private Category category;

    // epoch format
    private long date;
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "reimsUser", nullable = false)
    private ReimsUser reimsUser;

    // standard crud attributes
    private long createdAt;

    public enum Category {
        FUEL,
        PARKING
    }

}
