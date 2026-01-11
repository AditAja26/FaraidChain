package com.ems.estatemanagementsystem.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ems.estatemanagementsystem.pattern.AgencyFacade;
import com.ems.estatemanagementsystem.pattern.Observer;

import com.ems.estatemanagementsystem.dto.PICDTO;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.repository.PICRepository;
import com.ems.estatemanagementsystem.service.PICService;

@Service
public class PICServiceImpl implements PICService {

    private final PICRepository picRepository;
    private final List<Observer> observers = new ArrayList<>();

    @Autowired
    private AgencyFacade agencyFacade;

    public PICServiceImpl(PICRepository picRepository) {
        this.picRepository = picRepository;
    }

    @PostConstruct
    public void init() {
        this.registerObserver(agencyFacade);
    }

    @Override
    public PIC savePIC(PIC pic) {
        PIC savedPic = picRepository.save(pic);
        notifyObservers(savedPic);
        return savedPic;
    }

    @Override
    public List<PICDTO> findAll() {
        List<PIC> picList = picRepository.findAll();
        return picList.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
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

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        for (Observer observer : observers) {
            observer.update(arg);
        }
    }
}
