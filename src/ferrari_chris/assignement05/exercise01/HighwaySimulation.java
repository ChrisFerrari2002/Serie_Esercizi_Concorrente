package ferrari_chris.assignement05.exercise01;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Highway {
    public int entrances = 0;
    public int exits = 0;
    public int tolls = 0;
    public final Lock lock = new ReentrantLock();

    public void enter() {
        try {
            final Random random = new Random();
            // Generate random number between 1 and 5 ms
            final long randomValue = 1 + random.nextInt(5);
            Thread.sleep(randomValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int driveSection(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Random random = new Random();
        // generate random number between 10 (inclusive) and 21 (10 + 11) exclusive
        return random.nextInt(11) + 10;
    }
}

class Driver implements Runnable {
    private final int id;
    private final Highway highway;
    private final int delay;
    private int tollsPaid = 0;

    public Driver(int id, Highway state, int delay) {
        this.highway = state;
        this.delay = delay;
        this.id = id;
        this.tollsPaid = 0;
        System.out.println("Driver " + id + " created with " + delay + " ms of travel time");
    }

    public int getTollsPaid() {
        return tollsPaid;
    }

    public int getID() {
        return id;
    }

    @Override
    public void run() {
        System.out.println("Driver " + id + ": started");

        for (int i = 0; i < 500; i++) {
            highway.enter();

            highway.lock.lock();
            try {
                highway.entrances++;
                highway.lock.unlock();

                int sectionToll = highway.driveSection(delay);

                highway.lock.lock();
                highway.exits++;
                highway.tolls += sectionToll;
                highway.lock.unlock();

                tollsPaid += sectionToll;
                highway.lock.lock();
            } finally {
                highway.lock.unlock();
            }
        }
        System.out.println("Driver " + id + ": finished");
    }
}

public class HighwaySimulation {
    public static void main(final String[] args) {
        final Collection<Driver> drivers = new ArrayList<>();
        final Collection<Thread> threads = new ArrayList<>();
        final Random rand = new Random();

        // Create highway object
        final Highway highway = new Highway();

        for (int i = 0; i < 10; i++) {
            // Generate random number between 1 and 5 ms
            final int delay = 1 + rand.nextInt(5);
            // Create new driver instance with shared highway object and delay used as travel time
            final Driver newDriver = new Driver(i, highway, delay);
            drivers.add(newDriver);
            // Add new Thread to list of threads
            threads.add(new Thread(newDriver));
        }

        System.out.println("Simulation started");
        System.out.println("------------------------------------");

        // Start all threads
        threads.forEach(Thread::start);

        try {
            // Wait for threads to complete
            for (final Thread t : threads)
                t.join();
        } catch (final InterruptedException e) {
            return;
        }

        // Print results
        System.out.println("------------------------------------");
        System.out.println("Simulation finished");

        int totalTollsPaid = 0;
        for (final Driver a : drivers) {
            int paidTolls = a.getTollsPaid();
            totalTollsPaid += paidTolls;
            System.out.println("Driver " + a.getID() + " paid " + paidTolls);
        }

        System.out.println("Drivers total tolls paid : " + totalTollsPaid);
        System.out.println("Highway total tolls      : " + highway.tolls);
        System.out.println("Highway total entrances  : " + highway.entrances);
        System.out.println("Highway total exits      : " + highway.exits);
    }
}

