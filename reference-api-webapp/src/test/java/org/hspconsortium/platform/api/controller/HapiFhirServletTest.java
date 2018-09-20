package org.hspconsortium.platform.api.controller;

import org.junit.Test;

import javax.servlet.ServletException;

import static org.mockito.Mockito.mock;

public class HapiFhirServletTest {

    private HapiFhirServletContextHolder hapiFhirServletContextHolder = mock(HapiFhirServletContextHolder.class);

    private HapiFhirServlet hapiFhirServlet = new HapiFhirServlet();

    //Can't test this class until HapiFhirServletContextHolder is refactored to make it testable

//    @Test
//    public void initialize() throws ServletException {
//        hapiFhirServlet.initialize();
//    }

}
