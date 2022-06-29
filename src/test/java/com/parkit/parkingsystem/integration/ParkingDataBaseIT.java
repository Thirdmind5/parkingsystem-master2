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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        //2. I need to get the status of the parking spot 1 and see if it unavailable
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        int parkingSpotNumber = parkingSpot.getId();
        assertFalse (parkingSpotDAO.getParkingSpotStatus(parkingSpotNumber));
    }

    @Test
    public void testParkingLotExit(){
        //park a car -mock will use ABCDEF as the reg number
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        try{
            Thread.sleep(1000);
        }catch(Exception e){
            System.out.println("Error");
        }
        Date outTime = new Date();
        //exit the car - mock will use ABCDEF as the reg number
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        //checks if the exit time is correct
        assertEquals(ticket.getOutTime().getMinutes(), outTime.getMinutes());
        //checks if the fare is correct
        assertEquals(0,ticket.getPrice());
    }


    //this is an extra one I have added
    @Test
    public void testParkingSpotStatus(){
        //Checks if the ParkingSpotStatus is working fine
        //first park a car - mock will use ABCDEF as the reg number
        testParkingACar();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        int parkingSpotNumber = parkingSpot.getId();
        boolean result = parkingSpotDAO.getParkingSpotStatus(parkingSpotNumber);
        System.out.println("The result " + result);
        assertEquals(false,result);

    }

}
