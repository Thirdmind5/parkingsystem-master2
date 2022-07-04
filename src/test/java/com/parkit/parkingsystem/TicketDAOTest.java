package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

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

        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test //Save ticket
    public void testSaveTicket (){
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(10);
        ticket.setVehicleRegNumber("ABCDEF");
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        Date outTime = new Date();
        outTime.setTime(System.currentTimeMillis());
        ticket.setOutTime(outTime);
        ticketDAO.saveTicket(ticket);
        assertEquals(10,ticketDAO.getTicket("ABCDEF").getPrice());
    }

    @Test //Update Ticket
    public void testUpdateTicket(){
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        ticketDAO.saveTicket(ticket);
        //saved the ticket without price and out_time

        //now going to update the ticket with price and out_time
        Ticket newTicket = ticketDAO.getTicket("ABCDEF");
        newTicket.setPrice(111);
        Date outTime = new Date();
        outTime.setTime(System.currentTimeMillis());
        newTicket.setOutTime(outTime);
        ticketDAO.updateTicket(newTicket);
        assertEquals(111,ticketDAO.getTicket("ABCDEF").getPrice());

    }

    @Test //getTicketHistory
    public void testGetTicketHistory(){
    //park a car and exit and get the number of instances to be equal to 1
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(10);
        ticket.setVehicleRegNumber("ABCDEF");
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        Date outTime = new Date();
        outTime.setTime(System.currentTimeMillis());
        ticket.setOutTime(outTime);
        ticketDAO.saveTicket(ticket);
        ParkingSpot parkingSpot2 = new ParkingSpot(1,ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(10);
        ticket.setVehicleRegNumber("ABCDEF");
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ticket.setInTime(inTime);
        outTime.setTime(System.currentTimeMillis());
        ticket.setOutTime(outTime);
        ticketDAO.saveTicket(ticket);
        assertEquals(2,ticketDAO.getTicketHistory("ABCDEF"));
    }
}
