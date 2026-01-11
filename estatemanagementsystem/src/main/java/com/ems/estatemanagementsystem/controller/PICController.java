package com.ems.estatemanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.pattern.AgencyComponentFactory;
import com.ems.estatemanagementsystem.pattern.AgencyFacade;
import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.pattern.Subject;
import com.ems.estatemanagementsystem.service.ExternalAgencyService;
import com.ems.estatemanagementsystem.service.PICService;

@Controller
public class PICController implements Observer {

    private final PICService picService;
    private final ExternalAgencyService externalAgencyService;

    @Autowired
    private AgencyFacade agencyFacade;

    public PICController(PICService picService, ExternalAgencyService externalAgencyService) {
        this.picService = picService;
        this.externalAgencyService = externalAgencyService;
    }

    @GetMapping("/admin/pic/add/{externalAgencyId}")
    public String addingPIC(@PathVariable Long externalAgencyId, Model model) {
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);
        // Use Factory Method
        PIC pic = AgencyComponentFactory.createPIC();
        pic.setExternalAgencyInfo(externalAgency);
        model.addAttribute("pic", pic);
        model.addAttribute("externalAgency", externalAgency);
        return "addPIC";
    }

    @PostMapping("/admin/pic/add/{externalAgencyId}/done")
    public String savingPIC(@PathVariable Long externalAgencyId, @ModelAttribute PIC pic, Model model) {
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);

        // Use Factory Method
        PIC storedPIC = AgencyComponentFactory.createPIC();
        storedPIC.setPicName(pic.getPicName());
        storedPIC.setPicPhoneNum(pic.getPicPhoneNum());
        storedPIC.setPicEmail(pic.getPicEmail());
        storedPIC.setExternalAgencyInfo(externalAgency);

        // Register Observers (Facade and this Controller)
        storedPIC.registerObserver(agencyFacade);
        storedPIC.registerObserver(this);

        // Check for transaction hash from form (set by MetaMask JS)
        if (pic.getTxHash() != null && !pic.getTxHash().isEmpty()) {
            // This setter triggers notifyObservers() -> Facade logs to ledger & sends email
            storedPIC.setTxHash(pic.getTxHash());
            // Ensure status is updated if not already by setter logic (it is in entity)
        }

        picService.savePIC(storedPIC);
        model.addAttribute("successMessage", "PIC saved successfully!");
        return "redirect:/admin/externalAgency/list/";
    }

    @Override
    public void update(Subject subject) {
        // Controller acts as Observer: Log for now, or could trigger WebSocket update
        // to UI
        if (subject instanceof PIC) {
            System.out.println("PICController: Observer notified of PIC update for " + ((PIC) subject).getPicName());
        }
    }
}
