package com.ems.estatemanagementsystem.pattern;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;

public class AgencyComponentFactory {

    public static PIC createPIC() {
        return new PIC();
    }

    public static ExternalAgency createExternalAgency() {
        return new ExternalAgency();
    }
}
