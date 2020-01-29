package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;


@Table(name = "Parkings")
@Data
@DiscriminatorValue("PARKING")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parking extends Transaction {}
