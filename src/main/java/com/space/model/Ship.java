package com.space.model;
//TODO add lombok
//TODO add logger
//TODO change SQL to bind parameters
//TODO add Kafka MB
//TODO add docker
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Entity
@Table(name = "ship")
@DynamicUpdate
public class Ship implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String planet;
    @Enumerated(EnumType.STRING)
    private ShipType shipType;
    private Date prodDate;
    private Boolean isUsed;
    private Double speed;
    private Integer crewSize;
    private Double rating;

    public Ship(){}

    public Ship(Long id, String name, String planet, ShipType shipType, Date prodDate, Boolean isUsed, Double speed, Integer crewSize, Double rating) {
        this.id = id;
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.prodDate = prodDate;
        this.isUsed = isUsed;
        this.speed = speed;
        this.crewSize = crewSize;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public Boolean isUsed() {
        return isUsed;
    }

    public Double getSpeed() {
        return speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public void setRating() {
        if (prodDate == null || speed == null) return;
        double k = isUsed ? 0.5 : 1;
        Calendar calProdDate = new GregorianCalendar();
        calProdDate.setTime(prodDate);
        double r = (80 * speed * k) / (3019 - calProdDate.get(Calendar.YEAR) + 1);
        this.rating = new BigDecimal(r).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", planet='" + planet + '\'' +
                ", shipType=" + shipType +
                ", prodDate=" + prodDate +
                ", isUsed=" + isUsed +
                ", speed=" + speed +
                ", crewSize=" + crewSize +
                ", rating=" + rating +
                '}';
    }
}
