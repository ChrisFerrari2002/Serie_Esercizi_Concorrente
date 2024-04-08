package ferrari_chris.assignement06.exercise03;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

class TruckSpeedReader implements Runnable {
    private final int readerId;
    private int lastObservedSpeed;

    public TruckSpeedReader(final int readerId) {
        this.readerId = readerId;
        this.lastObservedSpeed = -1;
    }

    @Override
    public void run() {
        while (TruckSpeedMonitor.isMonitoring.get()) {
            // Update local speed if needed
            if (lastObservedSpeed != TruckSpeedMonitor.sharedSpeed) {
                lastObservedSpeed = TruckSpeedMonitor.sharedSpeed;
            } else {
                System.out.println("Reader" + readerId + ": (Last Speed: " + lastObservedSpeed + " km/h)");
            }
        }
    }
}

public class TruckSpeedMonitor {
    final static AtomicBoolean isMonitoring = new AtomicBoolean(true);
    static volatile int sharedSpeed = 0;
    private static int modifySharaedSpeed(){
        Random random = new Random();
        return random.nextInt(100);
    }

    public static void main(final String[] args) {
        final ArrayList<Thread> allReaderThreads = new ArrayList<>();

        // Create reader threads
        for (int i = 0; i < 10; i++)
            allReaderThreads.add(new Thread(new TruckSpeedReader(i)));

        // Start all reader threads
        for (final Thread readerThread : allReaderThreads)
            readerThread.start();

        // Simulate speed updates for 1000 iterations
        for (int i = 0; i < 1000; i++) {
            // Acquire lock to update shared speed
            sharedSpeed =  modifySharaedSpeed();// Simulating speed in km/h

            // Wait 1 ms between updates
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Set monitoring flag false to terminate reader threads
        isMonitoring.set(false);

        // Wait for all reader threads to complete
        for (final Thread readerThread : allReaderThreads)
            try {
                readerThread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

        System.out.println("Speed monitoring terminated.");
    }
}
