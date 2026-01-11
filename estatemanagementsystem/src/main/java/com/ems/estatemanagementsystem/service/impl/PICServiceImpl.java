package com.ems.estatemanagementsystem.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ems.estatemanagementsystem.dto.PICDTO;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.repository.PICRepository;
import com.ems.estatemanagementsystem.service.PICService;

@Service
public class PICServiceImpl implements PICService {

    private PICRepository picRepository;

    public PICServiceImpl(PICRepository picRepository) {
        this.picRepository = picRepository;
    }

    @Override
    public PIC savePIC(PIC pic) {
        // ExternalAgency externalAgency = pic.getExternalAgencyInfo();
        // if (externalAgency != null) {
        // // If the ExternalAgency associated with the PIC is not null,
        // // proceed with saving the PIC
        // externalAgencyService.saveNewPIC(pic, externalAgency);
        // } else {
        // // If the ExternalAgency is null, handle the case accordingly
        // // For example, you could throw an exception or log a warning
        // throw new IllegalArgumentException("ExternalAgency associated with the PIC is
        // null");
        // }
        return picRepository.save(pic);
    }

    @Override
    public List<PICDTO> findAll() {
        List<PIC> picList = picRepository.findAll();
        return picList.stream().map((pic) -> convertEntityToDto(pic))
                .collect(Collectors.toList());
    }

    @Override
    public PIC getPICById(Long id) {
        return picRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePICById(Long id) {
        picRepository.deleteById(id);
    }

    private PICDTO convertEntityToDto(PIC pic) {
        PICDTO picDTO = new PICDTO();
        picDTO.setId(pic.getId());
        picDTO.setPicName(pic.getPicName());
        picDTO.setPicEmail(pic.getPicEmail());
        picDTO.setPicPhoneNum(pic.getPicPhoneNum());
        picDTO.setExternalAgencyInfo(pic.getExternalAgencyInfo());

        return picDTO;
    }

}
