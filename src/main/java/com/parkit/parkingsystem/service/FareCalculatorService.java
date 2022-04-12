package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.dao.TicketDAO;

//import com.parkit.parkingsystem.constants.*;
//import com.parkit.parkingsystem.model.*;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //Check if the ticket is eligble for discount

        TicketDAO ticketDAO = new TicketDAO();
        double discount = 0;
       // int count = ticketDAO.getTicketHistory(ticket.getVehicleRegNumber());
       // if (count>1){
      //      discount =0.05;
//
     //   }

        @SuppressWarnings("deprecation")
		long inHour = ticket.getInTime().getTime();
        @SuppressWarnings("deprecation")
		long outHour = ticket.getOutTime().getTime();

        float duration = (float) (outHour - inHour)/3600000;



        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}