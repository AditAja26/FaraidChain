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
import com.ems.estatemanagementsystem.service.ExternalAgencyService;
import com.ems.estatemanagementsystem.service.PICService;

@Controller
public class PICController {

    private final PICService picService;
    private final ExternalAgencyService externalAgencyService;
    private final AgencyComponentFactory agencyComponentFactory;

    @Autowired
    public PICController(PICService picService, ExternalAgencyService externalAgencyService,
            AgencyComponentFactory agencyComponentFactory) {
        this.picService = picService;
        this.externalAgencyService = externalAgencyService;
        this.agencyComponentFactory = agencyComponentFactory;
    }

    @GetMapping("/admin/pic/add/{externalAgencyId}")
    public String addingPIC(@PathVariable Long externalAgencyId, Model model) {
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);
        // Use Factory Method
        PIC pic = agencyComponentFactory.createPIC();
        pic.setExternalAgencyInfo(externalAgency);
        model.addAttribute("pic", pic);
        model.addAttribute("externalAgency", externalAgency);
        return "addPIC";
    }

    @PostMapping("/admin/pic/add/{externalAgencyId}/done")
    public String savingPIC(@PathVariable Long externalAgencyId, @ModelAttribute PIC pic, Model model) {
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);

        // Use Factory Method
        PIC storedPIC = agencyComponentFactory.createPIC();
        storedPIC.setPicName(pic.getPicName());
        storedPIC.setPicPhoneNum(pic.getPicPhoneNum());
        storedPIC.setPicEmail(pic.getPicEmail());
        storedPIC.setExternalAgencyInfo(externalAgency);

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
}
