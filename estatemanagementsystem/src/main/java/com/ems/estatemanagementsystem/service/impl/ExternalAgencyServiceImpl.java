package com.ems.estatemanagementsystem.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ems.estatemanagementsystem.pattern.AgencyFacade;
import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.pattern.Subject;

import com.ems.estatemanagementsystem.dto.ExternalAgencyDTO;
import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.repository.ExternalAgencyRepository;
import com.ems.estatemanagementsystem.service.ExternalAgencyService;

@Service
public class ExternalAgencyServiceImpl implements ExternalAgencyService, Subject {

    private final ExternalAgencyRepository externalAgencyRepository;
    private final List<Observer> observers = new ArrayList<>();

    @Autowired
    private AgencyFacade agencyFacade;

    public ExternalAgencyServiceImpl(ExternalAgencyRepository externalAgencyRepository) {
        this.externalAgencyRepository = externalAgencyRepository;
    }

    @PostConstruct
    public void init() {
        this.registerObserver(agencyFacade);
    }

    @Override
    public ExternalAgency saveExternalAgency(ExternalAgency externalAgency) {
        ExternalAgency savedAgency = externalAgencyRepository.save(externalAgency);
        // Should we notify on creation? Usually logic was on TxHash update.
        // If TxHash is present on creation (which shouldn't happen for ExternalAgency
        // creation flow usually), we might want to notify.
        // Assuming updateExternalAgency is the main place for TxHash updates.
        return savedAgency;
    }

    @Override
    public List<ExternalAgencyDTO> getExternalAgencyList() {
        List<ExternalAgency> externalAgencyList = externalAgencyRepository.findAll();
        return externalAgencyList.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    private ExternalAgencyDTO convertEntityToDto(ExternalAgency externalAgency) {
        ExternalAgencyDTO externalAgencyDTO = new ExternalAgencyDTO();
        externalAgencyDTO.setId(externalAgency.getId());
        externalAgencyDTO.setAgencyName(externalAgency.getAgencyName());
        externalAgencyDTO.setEmail(externalAgency.getEmail());
        externalAgencyDTO.setAddress(externalAgency.getAddress());
        externalAgencyDTO.setPostcode(externalAgency.getPostcode());
        externalAgencyDTO.setState(externalAgency.getState());
        externalAgencyDTO.setDistrict(externalAgency.getDistrict());
        externalAgencyDTO.setPhoneNum(externalAgency.getPhoneNum());
        externalAgencyDTO.setServiceFee((float) externalAgency.getServiceFee());
        return externalAgencyDTO;
    }

    @Override
    public ExternalAgency getExternalAgencyById(Long externalAgencyId) {
        Optional<ExternalAgency> chosenExternalAgency = externalAgencyRepository.findById(externalAgencyId);

        if (chosenExternalAgency.isPresent()) {
            ExternalAgency currentExternalAgency = chosenExternalAgency.get();
            return currentExternalAgency;
        } else {
            return null;
        }
    }

    @Override
    public ExternalAgency updateExternalAgency(ExternalAgency externalAgency) {
        ExternalAgency existingExternalAgency = getExternalAgencyById(externalAgency.getId());

        existingExternalAgency.setId(externalAgency.getId());
        existingExternalAgency.setAgencyName(externalAgency.getAgencyName());
        existingExternalAgency.setEmail(externalAgency.getEmail());
        existingExternalAgency.setAddress(externalAgency.getAddress());
        existingExternalAgency.setPostcode(externalAgency.getPostcode());
        existingExternalAgency.setState(externalAgency.getState());
        existingExternalAgency.setPhoneNum(externalAgency.getPhoneNum());
        existingExternalAgency.setServiceFee(externalAgency.getServiceFee());

        // Also check if TxHash is being updated.
        if (externalAgency.getTxHash() != null) {
            existingExternalAgency.setTxHash(externalAgency.getTxHash());
        }

        ExternalAgency savedAgency = externalAgencyRepository.save(existingExternalAgency);
        notifyObservers(savedAgency);
        return savedAgency;
    }

    @Override
    public void deleteExternalAgencyById(Long externalAgencyId) {
        externalAgencyRepository.deleteById(externalAgencyId);
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
