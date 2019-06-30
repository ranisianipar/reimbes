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

    private long amount;
    private Category category;
    private Date date;
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_transaction", nullable = false)
    private ReimsUser user;

    // standard crud attributes
    private long createdAt;

    public enum Category {
        FUEL,
        PARKING
    }

}
