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

    public String processRegistrationDecision(String status, String txHash, String email) {
        String action;
        boolean hasHash = (txHash != null && !txHash.isEmpty());
        boolean isActive = "ACTIVE".equals(status);
        boolean hasEmail = (email != null && !email.isEmpty());

        // RULE 1: Complete and Blockchain Verified
        if (hasHash && isActive && hasEmail) {
            action = "LOG_TO_LEDGER_AND_SEND_EMAIL";
        }

        // RULE 2: Blockchain Verified but Inactive (Audit only)
        else if (hasHash && !isActive && hasEmail) {
            action = "LOG_TO_LEDGER_ONLY";
        }

        // RULE 3: Active but No Blockchain (Traditional Registration)
        else if (!hasHash && isActive && hasEmail) {
            action = "SEND_EMAIL_ONLY";
        }

        // RULE 4: Missing Critical Data (Default/Error)
        else {
            action = "REJECT_INVALID_DATA";
        }

        return action;
    }

    private void handlePICUpdate(PIC pic) {
        // Step 1: Query the Decision Table
        String action = processRegistrationDecision(pic.getStatus(), pic.getTxHash(), pic.getPicEmail());

        // Step 2: Execute Action Entries (Based on Table Result)
        switch (action) {
            case "LOG_TO_LEDGER_AND_SEND_EMAIL":
                logToLedger(pic.getTxHash());
                sendPICEmail(pic);
                break;
            case "LOG_TO_LEDGER_ONLY":
                logToLedger(pic.getTxHash());
                break;
            case "SEND_EMAIL_ONLY":
                sendPICEmail(pic);
                break;
            default:
                System.out.println("AgencyFacade: PIC logic ignored per decision table.");
        }
    }

    // Helper methods to keep the "Actions" clean
    private void logToLedger(String hash) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        contractService.saveContract(hash, AGENCY_CONTRACT_ADDRESS, timestamp);
    }

    private void sendPICEmail(PIC pic) {
        String subject = "PIC Registration Confirmed";
        String body = "Dear " + pic.getPicName() + ",\n\n" +
                "Your PIC registration via Blockchain is successful.\n" +
                "Transaction Hash: " + pic.getTxHash() + "\n\n" +
                "Status: ACTIVE";
        emailService.sendEmail(pic.getPicEmail(), subject, body);
    }

    private void handleExternalAgencyUpdate(ExternalAgency agency) {
        if ("ACTIVE".equals(agency.getStatus()) && agency.getTxHash() != null) {
            // 1. Log to Immutable Ledger (Contract)
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            contractService.saveContract(agency.getTxHash(), AGENCY_CONTRACT_ADDRESS,
                    timestamp);

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
