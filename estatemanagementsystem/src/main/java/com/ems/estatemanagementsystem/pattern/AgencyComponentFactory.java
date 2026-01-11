package com.ems.estatemanagementsystem.pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;

@Component
public class AgencyComponentFactory {

    public PIC createPIC() {
        PIC pic = new PIC();
        return pic;
    }

    public ExternalAgency createExternalAgency() {
        ExternalAgency agency = new ExternalAgency();
        return agency;
    }
}
