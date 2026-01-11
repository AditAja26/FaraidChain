package com.ems.estatemanagementsystem.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.pattern.Subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "external_agency")
public class ExternalAgency implements Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agencyName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String postcode;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String state;

    @OneToMany(mappedBy = "externalAgencyInfo")
    private List<PIC> pics;

    @Column(nullable = true)
    private float serviceFee;

    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "status")
    private String status = "PENDING";

    @Transient
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
        this.status = "ACTIVE";
        notifyObservers();
    }
}
