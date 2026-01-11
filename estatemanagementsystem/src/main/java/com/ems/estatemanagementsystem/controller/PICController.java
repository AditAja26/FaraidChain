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

        // 1. Factory Creation
        PIC storedPIC = agencyComponentFactory.createPIC();
        storedPIC.setPicName(pic.getPicName());
        storedPIC.setPicPhoneNum(pic.getPicPhoneNum());
        storedPIC.setPicEmail(pic.getPicEmail());
        storedPIC.setExternalAgencyInfo(externalAgency);

        // 2. Data Population
        if (pic.getTxHash() != null && !pic.getTxHash().isEmpty()) {
            storedPIC.setTxHash(pic.getTxHash());
        }

        // 3. Service call (Call Subject)
        picService.savePIC(storedPIC);
        model.addAttribute("successMessage", "PIC saved successfully!");
        return "redirect:/admin/externalAgency/list/";
    }
}
