package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

//import com.parkit.parkingsystem.constants.*;
//import com.parkit.parkingsystem.model.*;

public class FareCalculatorService {

//    public double getFare(double actualFare, boolean isDiscountApplicable){
//        if(isDiscountApplicable){
//          System.out.println("The discuount fare has been calculated");
//          return  actualFare - (actualFare * 0.05);
//        }else{
//            return actualFare;
//        }
//    };

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //Check if the ticket is eligble for discount
        @SuppressWarnings("deprecation")
		long inHour = ticket.getInTime().getTime();
        @SuppressWarnings("deprecation")
		long outHour = ticket.getOutTime().getTime();

        float duration = (float) (outHour - inHour)/3600000;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if(duration < 0.5){
                    ticket.setPrice(0);
                }else{
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                }
                break;
            }
            case BIKE: {
                if(duration < 0.5){
                    ticket.setPrice(0);
                }else {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void calculateDiscountedFare(Ticket ticket){
        calculateFare(ticket);
        ticket.setPrice(ticket.getPrice() * 0.95);
    }
}