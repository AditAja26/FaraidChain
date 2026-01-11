package com.ems.estatemanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.service.ExternalAgencyService;
import com.ems.estatemanagementsystem.service.PICService;
import com.ems.estatemanagementsystem.pattern.AgencyFacade;

@Controller
public class PICController {
    private final PICService picService;
    private final ExternalAgencyService externalAgencyService;
    private final AgencyFacade agencyFacade;

    public PICController(PICService picService, ExternalAgencyService externalAgencyService,
            AgencyFacade agencyFacade) {
        this.picService = picService;
        this.externalAgencyService = externalAgencyService;
        this.agencyFacade = agencyFacade;
    }

    @GetMapping("/admin/pic/add/{externalAgencyId}")
    public String addingPIC(@PathVariable Long externalAgencyId, Model model) {
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);
        PIC pic = new PIC();
        pic.setExternalAgencyInfo(externalAgency);
        model.addAttribute("pic", pic);
        model.addAttribute("externalAgency", externalAgency);
        return "addPIC";
    }

    @PostMapping("/admin/pic/add/{externalAgencyId}/done")
    public String savingPIC(@PathVariable Long externalAgencyId, @ModelAttribute PIC pic, Model model) {
        // Legacy method - kept for fallback or non-js usage if needed,
        // but the main flow is now AJAX based.
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);

        // Using Factory through Facade (Pattern Demonstration)
        PIC storedPIC = (PIC) agencyFacade.createComponent("PIC");

        storedPIC.setPicName(pic.getPicName());
        storedPIC.setPicPhoneNum(pic.getPicPhoneNum());
        storedPIC.setPicEmail(pic.getPicEmail());
        storedPIC.setExternalAgencyInfo(externalAgency);

        picService.savePIC(storedPIC);
        model.addAttribute("successMessage", "PIC saved successfully!");
        return "redirect:/externalAgency/list/";
    }

    // --- New AJAX Endpoints for Blockchain Flow ---

    @PostMapping("/admin/pic/save-draft/{externalAgencyId}")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> saveDraft(@PathVariable Long externalAgencyId, @RequestBody PIC picData) {
        ExternalAgency externalAgency = externalAgencyService.getExternalAgencyById(externalAgencyId);
        PIC storedPIC = (PIC) agencyFacade.createComponent("PIC");
        storedPIC.setPicName(picData.getPicName());
        storedPIC.setPicPhoneNum(picData.getPicPhoneNum());
        storedPIC.setPicEmail(picData.getPicEmail());
        storedPIC.setExternalAgencyInfo(externalAgency);

        PIC saved = picService.savePIC(storedPIC);

        Map<String, Long> response = new HashMap<>();
        response.put("id", saved.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/pic/finalize/{picId}")
    @ResponseBody
    public ResponseEntity<String> finalizePIC(@PathVariable Long picId, @RequestParam String txHash,
            @RequestParam String contractAddress) {
        try {
            // This triggers the Facade -> Observer -> ContractService flow
            agencyFacade.updatePICBlockchain(picId, txHash, contractAddress);
            return ResponseEntity.ok("Finalized");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/pic/rollback/{picId}")
    @ResponseBody
    public ResponseEntity<String> rollbackPIC(@PathVariable Long picId) {
        agencyFacade.rollbackPIC(picId);
        return ResponseEntity.ok("Rolled back");
    }
}
