package ferrari_chris.assignement03.exercise01;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class PublicRestrooms2 {
    private int usageCount;
    private int occupiedCount;
    private final int numAvailable;
    private int numOccupied;
    private Lock lock = new ReentrantLock();

    public PublicRestrooms2(int numRestrooms) {
        this.numAvailable = numRestrooms;
        this.numOccupied = 0;
        this.usageCount = 0;
        this.occupiedCount = 0;
    }
    public synchronized boolean checkAvaible(){
        if (numOccupied < numAvailable) {
            // Available restroom found: occupy
            numOccupied++;
            usageCount++;
        } else {
            // All restrooms occupied!
            occupiedCount++;
            return false;
        }
        return true;
    }
    public synchronized void leaveRestroom(){
        numOccupied--;
    }

    public boolean enter() {
        if (checkAvaible()){
            // use restroom: no protection required
            useRestroom();

            // leave restroom
            leaveRestroom();
            return true;
        } else {
            return false;
        }
    }

    private void useRestroom() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(5, 15));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getUsageCount() {
        return usageCount;
    }

    public int getOccupiedCount() {
        return occupiedCount;
    }
}

class User2 implements Runnable {
    private final int ID;
    private final PublicRestrooms restrooms;
    private int usageCount;
    private int occupiedCount;

    public User2(PublicRestrooms restrooms, int id) {
        this.restrooms = restrooms;
        this.ID = id;
        this.usageCount = 0;
        this.occupiedCount = 0;
    }

    @Override
    public void run() {
        System.out.println(this + " starting");
        for (int i = 0; i < 250; i++) {
            if (restrooms.enter())
                usageCount++;
            else
                occupiedCount++;

            // Simulate time before going the restroom again
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(1, 5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(this + " finished");
    }

    public int getUsageCount() {
        return usageCount;
    }

    public int getOccupiedCount() {
        return occupiedCount;
    }

    @Override
    public String toString() {
        return "User" + ID;
    }
}

public class A3Exercise1V2 {

    public static void main(final String[] args) {
        PublicRestrooms restRoom = new PublicRestrooms(2);

        final List<User> allUsers = IntStream.rangeClosed(1, 10).boxed()
                .map(id -> new User(restRoom, id))
                .peek(user -> System.out.println(user + " created."))
                .collect(Collectors.toList());
        final List<Thread> allThreads = allUsers.stream()
                .map(Thread::new)
                .peek(Thread::start)
                .collect(Collectors.toList());

        // Wait for all threads to terminate
        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        int userTotalUsageCount = 0;
        int userTotalOccupiedCount = 0;
        for (User user : allUsers) {
            userTotalUsageCount += user.getUsageCount();
            userTotalOccupiedCount += user.getOccupiedCount();
            System.out.println(
                    user + ": usages: " + user.getUsageCount() + " occupied: " + user.getOccupiedCount());
        }

        System.out.println("Usage recap");
        System.out.println("Total user usage count: " + userTotalUsageCount);
        System.out.println("Total restroom usage count: " + restRoom.getUsageCount());

        System.out.println("Occupacy recap");
        System.out.println("Total user occupied count: " + userTotalOccupiedCount);
        System.out.println("Total restroom occupied count: " + restRoom.getOccupiedCount());
    }
}
