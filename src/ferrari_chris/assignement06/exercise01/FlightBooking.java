package ferrari_chris.assignement06.exercise01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Flight {
    private String flightNumber;
    private int numRows;
    private int numSeatsPerRow;
    private int numSeatsAvailable;
    private boolean[][] seats;
    private Lock lock = new ReentrantLock();


    public Flight(String flightNumber, int numRows, int numSeatsPerRow) {
        this.flightNumber = flightNumber;
        this.numRows = numRows;
        this.numSeatsPerRow = numSeatsPerRow;
        this.numSeatsAvailable = numRows * numSeatsPerRow;
        this.seats = new boolean[numRows][numSeatsPerRow];
    }

    public String toString() {
        return String.format("Flight %s (%d seats available)", flightNumber, numSeatsAvailable);
    }
    public boolean updateNumSeatsAvailable(){
        Random random = new Random();
        lock.lock();
        try {
            final int row = random.nextInt(numRows);
            final int seat = random.nextInt(numSeatsPerRow);

            if (!seats[row][seat]) {
                seats[row][seat] = true;
                numSeatsAvailable--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    public synchronized boolean checkAvailability(){
        return numSeatsAvailable > 0;
    }

}

class Passenger implements Runnable {
    private final int id;
    private final Flight flight;

    public Passenger(int id, Flight flight) {
        this.id = id;
        this.flight = flight;
    }

    @Override
    public void run() {
        Random random = new Random();
        int numTickets = 0;

        while (flight.checkAvailability()) {
            if (flight.updateNumSeatsAvailable()){
                System.out.println(flight);
                numTickets++;
            }


            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Passenger%d booked %d tickets %n", id, numTickets);
    }
}

public class FlightBooking {
    public static void main(String[] args) {
        Flight flight = new Flight("AB1234", 30, 6);
        System.out.println(flight);

        List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            allThreads.add(new Thread(new Passenger(i, flight)));

        allThreads.forEach(Thread::start);

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
