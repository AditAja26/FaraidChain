package com.ems.estatemanagementsystem.service;

import java.util.List;

import com.ems.estatemanagementsystem.dto.PICDTO;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.pattern.Subject;

public interface PICService extends Subject {

    PIC savePIC(PIC pic);

    List<PICDTO> findAll();

}
