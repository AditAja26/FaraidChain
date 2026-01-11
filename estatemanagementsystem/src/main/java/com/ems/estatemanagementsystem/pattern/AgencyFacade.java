package com.ems.estatemanagementsystem.pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.service.ContractService;
import com.ems.estatemanagementsystem.service.emailservice.EmailService;
import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.pattern.Subject;

@Component
public class AgencyFacade implements Observer {

    @Autowired
    private ContractService contractService;

    @Autowired
    private EmailService emailService;

    // Contract Address (Ideally from config, but hardcoded here as per JS)
    private static final String AGENCY_CONTRACT_ADDRESS = "0xd9145CCE52D386f254917e481eB44e9943F39138";

    // In a real scenario, we might have a dedicated LedgerService,
    // but here we reuse ContractService as per the plan/context for logging
    // transactions.

    @Override
    public void update(Object arg) {
        if (arg instanceof PIC) {
            handlePICUpdate((PIC) arg);
        } else if (arg instanceof ExternalAgency) {
            handleExternalAgencyUpdate((ExternalAgency) arg);
        }
    }

    private void handlePICUpdate(PIC pic) {
        if ("ACTIVE".equals(pic.getStatus()) && pic.getTxHash() != null) {
            // 1. Log to Immutable Ledger (Contract)
            contractService.saveContract(pic.getTxHash(), AGENCY_CONTRACT_ADDRESS,
                    java.time.LocalDate.now().toString());

            // 2. Send Notification Email
            String subject = "PIC Registration Confirmed";
            String body = "Dear " + pic.getPicName() + ",\n\n" +
                    "Your registration via Blockchain is successful.\n" +
                    "Exteral Agency: "
                    + (pic.getExternalAgencyInfo() != null ? pic.getExternalAgencyInfo().getAgencyName() : "N/A") + "\n"
                    +
                    "Transaction Hash: " + pic.getTxHash() + "\n\n" +
                    "Status: ACTIVE";
            emailService.sendEmail(pic.getPicEmail(), subject, body);

            System.out.println("AgencyFacade: PIC updated. Ledger logged and Email sent.");
        }
    }

    private void handleExternalAgencyUpdate(ExternalAgency agency) {
        if ("ACTIVE".equals(agency.getStatus()) && agency.getTxHash() != null) {
            // 1. Log to Immutable Ledger (Contract)
            contractService.saveContract(agency.getTxHash(), AGENCY_CONTRACT_ADDRESS,
                    java.time.LocalDate.now().toString());

            // 2. Send Notification Email
            String subject = "Agency Registration Confirmed";
            String body = "Dear " + agency.getAgencyName() + ",\n\n" +
                    "Your agency registration via Blockchain is successful.\n" +
                    "Transaction Hash: " + agency.getTxHash() + "\n\n" +
                    "Status: ACTIVE";

            emailService.sendEmail(agency.getEmail(), subject, body);

            System.out.println("AgencyFacade: ExternalAgency updated. Ledger logged and Email sent.");
        }
    }
}
