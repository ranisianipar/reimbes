package com.reimbes;

import lombok.Data;
import net.bytebuddy.build.ToStringPlugin;

import javax.persistence.*;

@Data
@Entity(name = "transactions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TRANSACTION_TYPE")
public class Transaction {

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

    private String location;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @ToStringPlugin.Exclude
    @JoinColumn(name = "reimsUser", nullable = false)
    private ReimsUser reimsUser;

    // standard crud attributes
    private long createdAt;

    public enum Category {
        FUEL,
        PARKING
    }

}
