package com.ems.estatemanagementsystem.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "external_agency")
public class ExternalAgency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agencyName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String postcode;

    @Column(nullable = false)
    private String state;

    @Column(nullable = true)
    private String district;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private double serviceFee;

    @JsonIgnore
    @OneToMany(mappedBy = "externalAgencyInfo", cascade = CascadeType.ALL)
    private List<PIC> pic;

    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "status")
    private String status = "PENDING";

    public void setTxHash(String txHash) {
        this.txHash = txHash;
        this.status = "ACTIVE";
    }
}
