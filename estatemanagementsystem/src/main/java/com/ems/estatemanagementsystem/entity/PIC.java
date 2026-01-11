package com.ems.estatemanagementsystem.entity;

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
public class PIC {

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

    // Helper to trigger status update
    public void setTxHash(String txHash) {
        this.txHash = txHash;
        this.status = "ACTIVE"; // Activate on valid hash
    }
}
