package com.ems.estatemanagementsystem.pattern;

import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.entity.PIC;
import org.springframework.stereotype.Component;

@Component
public class AgencyComponentFactory {

    public Object createComponent(String type) {
        if (type == null) {
            return null;
        }
        if (type.equalsIgnoreCase("PIC")) {
            return new PIC();
        } else if (type.equalsIgnoreCase("AGENCY")) {
            return new ExternalAgency();
        }
        return null;
    }
}
