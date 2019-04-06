package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long id;

    // standard crud attributes
    private Date createdDate;
    private Date updatedDate;

    private Date date;
    private long amount;
    private Category category;

    // user one to many transaction
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReimsUser user;

    // jadi kalo orang ga ngirim foto dariawal/manual ga bisa nambahin foto
    @Column(updatable = false)
    private String imagePath;

    public enum Category {
        FUEL,
        PARKING
    }

    public Transaction() {
        this.createdDate = new Date();
    }


}
