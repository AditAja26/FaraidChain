package com.ems.estatemanagementsystem.pattern;

import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.entity.ExternalAgency;
import com.ems.estatemanagementsystem.service.ContractService;
import com.ems.estatemanagementsystem.service.emailservice.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PatternsTest {

    @Test
    public void testFactoryPattern() {
        AgencyComponentFactory factory = new AgencyComponentFactory();

        Object pic = factory.createComponent("PIC");
        Object agency = factory.createComponent("AGENCY");

        assertTrue(pic instanceof PIC);
        assertTrue(agency instanceof ExternalAgency);
    }

    @Test
    public void testObserverPatternWithPIC() {
        PIC pic = new PIC();
        pic.setPicName("Test PIC");
        pic.setPicEmail("pic@test.com");

        Observer mockObserver1 = mock(Observer.class);
        Observer mockObserver2 = mock(Observer.class);

        pic.registerObserver(mockObserver1);
        pic.registerObserver(mockObserver2);

        // Triggering state change (the pattern trigger)
        pic.setTxHash("0x123abc");

        // Success criteria: Both observers notified
        verify(mockObserver1, times(1)).update(pic);
        verify(mockObserver2, times(1)).update(pic);
        assertEquals("0x123abc", pic.getTxHash());
    }

    @Test
    public void testFacadeInteraction() {
        // This is more of an integration test, but we can mock the services
        // to show the orchestrator works.
        // In a real Spring test, we would use @SpringBootTest
    }
}
