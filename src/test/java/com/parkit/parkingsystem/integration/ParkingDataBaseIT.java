package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        //this object to clear the ticket table and sets all parking as available in parking table
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability

        //1. First check if a ticket with car registration "ABCDEF" is in the tickets table

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assert ticket != null;

        //2. I need to get the status of the parking spot 1 and see if it unavilable
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        int parkingSpotNumber = parkingSpot.getId();

        //Test code to see to check if we are getting correct parking spot status
        // System.out.println("The parking spot Number is: " + parkingSpotNumber);
        //System.out.println("The parking spot status for 1 is " + parkingSpotDAO.getParkingSportStatus(parkingSpotNumber));
        // System.out.println("The parking spot status for 2 is " + parkingSpotDAO.getParkingSportStatus(2));

        assert !parkingSpotDAO.getParkingSportStatus(parkingSpotNumber);
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        try{
            Thread.sleep(1000);
        }catch(Exception e){
            System.out.println("Error");
        }
        Date outTime = new Date();
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database

        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        //System.out.println(ticket.getPrice() + "Price of ticket");

        //System.out.println(ticket.getOutTime().getMinutes() + " this is the outtime");
        //System.out.println(outTime.getMinutes() + " this is the time");
        assertEquals(ticket.getOutTime().getMinutes(), outTime.getMinutes());

        assertEquals(0,ticket.getPrice());

    }

    @Test
    public void testParkingSpotStatus(){

        testParkingACar();
        //ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //try{
        //    Thread.sleep(1000);
        //}catch(Exception e){
        //    System.out.println("Error");
        //}
        //Date outTime = new Date();
        //parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database

        //Ticket ticket = ticketDAO.getTicket("ABCDEF");

        boolean result = parkingSpotDAO.getParkingSportStatus(6);
        System.out.println("The result " + result);
        assertEquals(false,result);


    }

}
