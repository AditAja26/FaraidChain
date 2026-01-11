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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "pic")
public class PIC implements Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String picName;

    @Column(nullable = false)
    private String picPhoneNum;

    @Column(nullable = false)
    private String picEmail;

    @ManyToOne
    @JoinColumn(name = "external_agency_id")
    private ExternalAgency externalAgencyInfo;

    @Column
    private String txHash;

    @Transient // Not saved in PIC table, passed to Observers
    private String contractAddress;

    @Transient
    private List<Observer> observers = new ArrayList<>();

    public void setTxHash(String txHash) {
        this.txHash = txHash;
        notifyObservers(this);
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getContractAddress() {
        return this.contractAddress;
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
