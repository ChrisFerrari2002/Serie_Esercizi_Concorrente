package ferrari_chris.assignement02.exercise01;

import java.util.ArrayList;
import java.util.List;

// Represents a rally car participating in the race
class RallyCar implements Runnable {
    final int carNumber;

    RallyCar(int carNumber) {
        this.carNumber = carNumber;
    }

    @Override
    public void run() {
        System.out.printf("RallyCar #%d waiting for start signal...%n", carNumber);
        while (RallySimulation.startSignal != carNumber) {
            // Busy wait until start signal matches this car's number
        }
        System.out.printf("RallyCar #%d starting.%n", carNumber);
        final long duration = 500 + (long) (Math.random() * 1000);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("RallyCar #%d finished the race.%n", carNumber);
    }
}

public class RallySimulation {
    private static final int NUM_CARS = 10;
    static volatile int startSignal = 0;

    public static void main(String[] args) {
        List<Thread> allThreads = new ArrayList<>();
        for (int i = 1; i <= NUM_CARS; i++) {
            allThreads.add(new Thread(new RallyCar(i)));
        }

        // Start all rally car threads
        allThreads.forEach(Thread::start);

        // Simulate marshal signaling the start of the race for each car
        for (int i = 1; i <= NUM_CARS; i++) {
            try {
                Thread.sleep(1000); // Wait for 1 second between signaling each car
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Marshal: Car #%d can start now!%n", i);
            startSignal = i; // Signal the car to start by updating startSignal
        }

        // Wait for all threads to finish
        try {
            for (Thread thread : allThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Rally simulation terminated");
    }
}
