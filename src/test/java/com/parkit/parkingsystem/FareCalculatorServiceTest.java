package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;

import static java.lang.Float.parseFloat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.math.RoundingMode;
import java.text.DecimalFormat;


@ExtendWith(MockitoExtension.class)
public class  FareCalculatorServiceTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    public static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;


    @BeforeAll
    private static void setUp() {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        fareCalculatorService = new FareCalculatorService(ticketDAO);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        ticket = new Ticket();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void calculateFareCar() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

//    @Test
//    public void testDiscountedFare() throws Exception {
//
//        when(inputReaderUtil.readSelection()).thenReturn(1);
//        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("DDDD");
//        parkingSpotDAO = new ParkingSpotDAO();
//        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
//        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//
//        parkingService.processIncomingVehicle();
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            System.out.println("Error");
//        }
//        parkingService.processExitingVehicle(); //now the vehicle "DDDD" has been parked and exited
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            System.out.println("Error");
//        }
//        parkingService.processIncomingVehicle();//parking "DDDD" second time
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            System.out.println("Error");
//        }
//        parkingService.processExitingVehicle();//now the vehicle "DDDD" is twice in the DB
//        Date inTime = new Date();
//        inTime.setTime(System.currentTimeMillis() - (2 * 60 * 60 * 1000)); //two hours parking
//        Ticket ticketNew = ticketDAO.getTicket("DDDD");
//        ticketNew.setInTime(inTime);
//        fareCalculatorService.calculateFare(ticketNew);
//        final DecimalFormat df = new DecimalFormat("0.00");
//        //assert(2.85 - ticketNew.getPrice() <0.1);
//        String s = "2.85";
//        assertEquals(s, df.format(ticketNew.getPrice()));
//    }

    @Test
    public void testDiscountedFareCar2() throws Exception {

        TicketDAO ticketDAO = Mockito.mock(TicketDAO.class);
        when(ticketDAO.getTicketHistory("DDDD")).thenReturn(3);
        FareCalculatorService fareCalculatorService1 = new FareCalculatorService(ticketDAO);

        ticket.setVehicleRegNumber("DDDD");
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (2 *60 * 60 * 1000)); //two hours parking
        Ticket ticketNew = new Ticket();
        ticketNew.setInTime(inTime);
        Date now = new Date();
        ticketNew.setOutTime(now);
        ticketNew.setParkingSpot(parkingSpot);
        ticketNew.setVehicleRegNumber("DDDD");
        fareCalculatorService1.calculateFare(ticketNew);
        final DecimalFormat df = new DecimalFormat("0.00");
        assertEquals(2.85, ticketNew.getPrice());

    }


    @Test
    public void testDiscountedFareBike2() throws Exception {

        TicketDAO ticketDAO = Mockito.mock(TicketDAO.class);
        when(ticketDAO.getTicketHistory("DDDD")).thenReturn(3);
        FareCalculatorService fareCalculatorService1 = new FareCalculatorService(ticketDAO);

        ticket.setVehicleRegNumber("DDDD");
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (2 *60 * 60 * 1000)); //two hours parking
        Ticket ticketNew = new Ticket();
        ticketNew.setInTime(inTime);
        Date now = new Date();
        ticketNew.setOutTime(now);
        ticketNew.setParkingSpot(parkingSpot);
        ticketNew.setVehicleRegNumber("DDDD");
        fareCalculatorService1.calculateFare(ticketNew);
        final DecimalFormat df = new DecimalFormat("0.00");
        assertEquals(1.9, ticketNew.getPrice());

    }

//    @Test
//    public void testLessThan30MinDisount() throws Exception {
//
//        when(inputReaderUtil.readSelection()).thenReturn(1);
//        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("DDDD");
//        parkingSpotDAO = new ParkingSpotDAO();
//        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
//        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//
//       parkingService.processIncomingVehicle();
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            System.out.println("Error");
//        }
//       parkingService.processExitingVehicle();
//
//        Date inTime = new Date();
//        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000)); //two hours parking
//        Ticket ticketNew = ticketDAO.getTicket("DDDD");
//        ticketNew.setInTime(inTime);
//
//        fareCalculatorService.calculateFare(ticketNew);
//        final DecimalFormat df = new DecimalFormat("0.00");
//        //assert(2.85 - ticketNew.getPrice() <0.1);
//        //String s = "2.85";
//        assertEquals(0, ticketNew.getPrice());
//    }

    @Test
    public void testLessThan30MinDisountCar2() throws Exception {
       TicketDAO ticketDAO2 = Mockito.mock(TicketDAO.class);
       when(ticketDAO2.getTicket("DDDD")).thenReturn(ticket);
       ticket.setVehicleRegNumber("DDDD");
       ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);

       Date inTime = new Date();
       inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000)); //two hours parking
        Ticket ticketNew = ticketDAO2.getTicket("DDDD");
        ticketNew.setInTime(inTime);
        Date now = new Date();
        ticketNew.setOutTime(now);
        ticketNew.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticketNew);
        final DecimalFormat df = new DecimalFormat("0.00");
        assertEquals(0, ticketNew.getPrice());
    }

    @Test
    public void testLessThan30MinDisountBike2() throws Exception {
        TicketDAO ticketDAO2 = Mockito.mock(TicketDAO.class);
        when(ticketDAO2.getTicket("DDDD")).thenReturn(ticket);
        ticket.setVehicleRegNumber("DDDD");
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000)); //two hours parking
        Ticket ticketNew = ticketDAO2.getTicket("DDDD");
        ticketNew.setInTime(inTime);
        Date now = new Date();
        ticketNew.setOutTime(now);
        ticketNew.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticketNew);
        final DecimalFormat df = new DecimalFormat("0.00");
        assertEquals(0, ticketNew.getPrice());
    }


}
