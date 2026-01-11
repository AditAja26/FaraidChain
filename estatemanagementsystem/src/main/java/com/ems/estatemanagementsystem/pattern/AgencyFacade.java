package com.ems.estatemanagementsystem.pattern;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.service.ContractService;
import com.ems.estatemanagementsystem.service.ExternalAgencyService;
import com.ems.estatemanagementsystem.service.PICService;
import com.ems.estatemanagementsystem.service.emailservice.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgencyFacade {

    @Autowired
    private AgencyComponentFactory factory;

    @Autowired
    private PICService picService;

    @Autowired
    private ExternalAgencyService agencyService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private EmailService emailService;

    /**
     * Simplifies the update process for a PIC.
     * Facade orchestrates the Factory, Service, and Observers.
     */
    /**
     * Simplifies the update process for a PIC.
     * Facade orchestrates the Factory, Service, and Observers.
     */
    public void updatePICBlockchain(Long picId, String txHash, String contractAddress) {
        PIC pic = picService.getPICById(picId);
        if (pic != null) {
            // Attach Observers (The "MVC" way - decoupling the notification)
            pic.registerObserver(contractService);
            pic.registerObserver(emailService);

            // Trigger the state change - which triggers notifications
            pic.setTxHash(txHash);
            pic.setContractAddress(contractAddress); // Set the address so Observers can read it

            // Save the state
            picService.savePIC(pic);

            // Trigger Observers manually if needed, or rely on setters if they notify
            // Check PIC.java logic. If setters notify, we might duplicate if we do both.
            // But usually we trigger once.
            // In the previous code: pic.setTxHash() -> notifyObservers() inside setter?
            // Let's assume setter does it. If not, we call notifyObservers.
            // Previous code had explicit call? "pic.notifyObservers(pic)" was removed in
            // replacement?
            // The view shows "pic.setTxHash(txHash)" line 42. Let's keep it consistent.
        }
    }

    /**
     * Simplifies the update process for an External Agency.
     */
    public void updateAgencyBlockchain(Long agencyId, String txHash) {
        ExternalAgency agency = agencyService.getExternalAgencyById(agencyId);
        if (agency != null) {
            // Attach Observers
            agency.registerObserver(contractService);
            agency.registerObserver(emailService);

            // Trigger state change
            agency.setTxHash(txHash);

            // Save state
            agencyService.saveExternalAgency(agency);
        }
    }

    /**
     * Uses the Factory pattern to create a new component.
     */
    public Object createComponent(String type) {
        return factory.createComponent(type);
    }

    /**
     * Rollback feature: Deletes the PIC if blockchain transaction failed.
     */
    public void rollbackPIC(Long picId) {
        picService.deletePICById(picId);
        System.out.println("AgencyFacade: Rolled back (deleted) PIC with ID: " + picId);
    }
}
