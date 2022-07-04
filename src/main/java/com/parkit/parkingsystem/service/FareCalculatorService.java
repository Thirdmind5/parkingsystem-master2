package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    private TicketDAO ticketDAO;

    public FareCalculatorService(TicketDAO ticketDAO){
        this.ticketDAO = ticketDAO;
    }
//    TicketDAO ticketDAO = new TicketDAO();

    public double getFare(double actualFare, boolean isDiscountApplicable) {
        if (isDiscountApplicable) {
            System.out.println("The discuount fare has been calculated");
            return actualFare - (actualFare * 0.05);
        } else {
            return actualFare;
        }
    }

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        //Check if the ticket is eligble for discount
        String regNum = ticket.getVehicleRegNumber();
        int numOfTimesParked = ticketDAO.getTicketHistory(regNum);
        boolean applyDiscount = (numOfTimesParked >= 2);
        @SuppressWarnings("deprecation")
        long inHour = ticket.getInTime().getTime();
        @SuppressWarnings("deprecation")
        long outHour = ticket.getOutTime().getTime();
        float duration = (float) (outHour - inHour) / 3600000;
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (duration < 0.5) {
                    ticket.setPrice(0);
                } else {
                    ticket.setPrice(getFare(duration * Fare.CAR_RATE_PER_HOUR, applyDiscount));
                }
                break;
            }
            case BIKE: {
                if (duration < 0.5) {
                    ticket.setPrice(0);
                } else {
                    ticket.setPrice(getFare(duration * Fare.BIKE_RATE_PER_HOUR, applyDiscount));
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}