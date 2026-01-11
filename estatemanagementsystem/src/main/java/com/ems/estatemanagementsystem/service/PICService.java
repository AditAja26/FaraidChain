package com.ems.estatemanagementsystem.service;

import java.util.List;

import com.ems.estatemanagementsystem.dto.PICDTO;
import com.ems.estatemanagementsystem.entity.PIC;

public interface PICService {

    PIC savePIC(PIC pic);

    List<PICDTO> findAll();

    PIC getPICById(Long id);

    void deletePICById(Long id);
}
