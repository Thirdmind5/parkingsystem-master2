package com.parkit.parkingsystem;

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


public class ParkingSpotDAOTest {

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

    @Test
    public void testParkingSpotStatus(){

        boolean result = parkingSpotDAO.getParkingSpotStatus(6);
        System.out.println("The result " + result);
        assertEquals(false,result);

    }
    @Test
    public void testUpdateParking(){
        //UpdateParking spot only frees the spot
        //I first need to use a spot, check if it is available and make sure it is not
        //then use the UpdateParking to free it up

        ParkingType parkingType = ParkingType.CAR;

        int parkingSpotNum = parkingSpotDAO.getNextAvailableSlot(parkingType);

        System.out.println(parkingSpotNum);

        assertEquals(1,parkingSpotNum);

        //we now know that parking spot 1 is taken
        //paring spot 1 needs to be made avaiable using UpdateParking

        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);

        assertEquals(true,parkingSpotDAO.updateParking(parkingSpot));

    }

}
