package ferrari_chris.assignement07.exercise03;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Timer {
    private long startTime = -1;
    private long stopTime = -1;

    final public void start() {
        startTime = System.currentTimeMillis();
    }

    final public void stop() {
        stopTime = System.currentTimeMillis();
    }

    final public long getElapsedTime() {
        if (startTime < 0 || stopTime < 0)
            return 0;
        return stopTime - startTime;
    }
}

class Restroom {
    private boolean isOccupied = false;

    // Return false in case restroom is already occupied
    public boolean tryToOccupy() {
        // Simulate testing
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (isOccupied)
            return false;
        this.isOccupied = true;
        return true;
    }

    public void leave() {
        this.isOccupied = false;
    }
}

class PublicRestrooms {
    private final Lock lock = new ReentrantLock();
    private final Restroom maleRestrooms[];
    private final Restroom femaleRestrooms[];

    public PublicRestrooms(final int nMaleRestrooms, final int nFemaleRestrooms) {
        this.maleRestrooms = new Restroom[nMaleRestrooms];
        for (int i = 0; i < nMaleRestrooms; i++)
            this.maleRestrooms[i] = new Restroom();

        this.femaleRestrooms = new Restroom[nFemaleRestrooms];
        for (int i = 0; i < nFemaleRestrooms; i++)
            this.femaleRestrooms[i] = new Restroom();
    }

    public boolean occupy(final boolean isMale) {
        Restroom occupiedRestroom = null;

        lock.lock();
        try {
            if (isMale) {
                // Find first male restroom available
                for (int i = 0; i < maleRestrooms.length; i++) {
                    final Restroom restRoom = maleRestrooms[i];
                    if (restRoom.tryToOccupy()) {
                        occupiedRestroom = restRoom;
                        break;
                    }
                }
            } else {
                // Find first female restroom available
                for (int i = 0; i < femaleRestrooms.length; i++) {
                    final Restroom restRoom = femaleRestrooms[i];
                    if (restRoom.tryToOccupy()) {
                        occupiedRestroom = restRoom;
                        break;
                    }
                }
            }
        } finally {
            lock.unlock();
        }

        // all restrooms occupied!
        if (occupiedRestroom == null)
            return false;

        try {
            Thread.sleep(80);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        // Leave restroom
        lock.lock();
        try {
            occupiedRestroom.leave();
        } finally {
            lock.unlock();
        }
        return true;
    }
}

class User implements Runnable {
    final boolean isMale;
    final int id;
    final PublicRestrooms publicRestrooms;

    public User(final PublicRestrooms restrooms, final int id, final boolean isMale) {
        this.id = id;
        this.isMale = isMale;
        this.publicRestrooms = restrooms;
    }

    @Override
    public void run() {
        long totalTime = 0;
        int drinksCount = 0;
        int count = 0;
        while (count < 20) {
            try {
                Thread.sleep(5);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            // Have a drink
            if (ThreadLocalRandom.current().nextBoolean())
                drinksCount++;

            if (drinksCount > 10) {
                final Timer timer = new Timer();
                timer.start();
                // Keep trying until restroom available
                while (true) {
                    final boolean done = publicRestrooms.occupy(isMale);
                    if (done)
                        break;
                }

                timer.stop();
                totalTime += timer.getElapsedTime();
                log("took " + timer.getElapsedTime() + " ms.");
                drinksCount = 0;
                count++;
            }
        }

        final double avg = totalTime / count;
        log("total time spent in the restroom: " + totalTime + " (avg " + avg + ")");
    }

    private void log(final String msg) {
        String pre = isMale ? "Mr" : "Ms";
        System.out.printf("%s %s: %s%n", pre, id, msg);
    }
}

public class RestroomsSimulation {
    public static void main(final String[] args) {
        final Timer mainTimer = new Timer();
        final Collection<Thread> threads = new ArrayList<>();
        final PublicRestrooms restroom = new PublicRestrooms(3, 3);

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(new User(restroom, i, true)));
            threads.add(new Thread(new User(restroom, i, false)));
        }

        System.out.println("Simulation started");
        System.out.println("------------------------------------");

        mainTimer.start();
        // Start all threads
        threads.stream().forEach(Thread::start);


        // Wait all thread to terminate
        for (final Thread t : threads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                // Do nothing
            }
        mainTimer.stop();

        System.out.println("------------------------------------");

        System.out.println("Simulation took : " + mainTimer.getElapsedTime() + " ms");
        System.out.println("Simulation finished");
    }
}