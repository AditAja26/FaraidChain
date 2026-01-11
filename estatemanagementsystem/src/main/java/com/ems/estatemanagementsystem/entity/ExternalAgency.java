package com.ems.estatemanagementsystem.entity;

import java.util.ArrayList;
import java.util.List;
import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.pattern.Subject;
import jakarta.persistence.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Column
    private String txHash;

    @Transient
    private String contractAddress;

    @Transient
    private List<Observer> observers = new ArrayList<>();

    public void setTxHash(String txHash) {
        this.txHash = txHash;
        notifyObservers(this);
    }

    @Override
    public void registerObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object data) {
        for (Observer observer : observers) {
            observer.update(data);
        }
    }
}
