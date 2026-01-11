package com.ems.estatemanagementsystem.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @JoinColumn(name = "state_info_id")
    private ExternalAgency externalAgencyInfo;

    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "status")
    private String status = "PENDING"; // Default status

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

    // Helper to trigger update (e.g., when txHash is set)
    public void setTxHash(String txHash) {
        this.txHash = txHash;
        this.status = "ACTIVE"; // Activate on valid hash
        notifyObservers();
    }
}
