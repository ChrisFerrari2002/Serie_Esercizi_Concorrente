package assignement10.exercise01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Fork {
    private boolean taken = false;

    public synchronized boolean take() {
        if (taken) {
            return false;
        }
        taken = true;
        return true;
    }

    public synchronized void release() {
        taken = false;
    }
}

class Philosopher implements Runnable {
    private final int id;

    public Philosopher(final int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                think();
                pickUpForks();
                eat();
                putDownForks();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void think() throws InterruptedException {
        System.out.println(this + " is thinking");
        Thread.sleep(Philosophers.UNIT_OF_TIME * (ThreadLocalRandom.current().nextInt(6)));
    }

    private void eat() throws InterruptedException {
        System.out.println(this + " is eating");
        Thread.sleep(Philosophers.UNIT_OF_TIME * 1);
    }

    private void pickUpForks() throws InterruptedException {
        // Wait until left fork has been picked up
        while (!pickUpFork(id))
            ;
        Thread.sleep(Philosophers.UNIT_OF_TIME * 5);
        // Wait until right fork has been picked up
        while (!pickUpFork((id + 1) % Philosophers.NUM_PHILOSOPHERS))
            ;
    }

    private boolean pickUpFork(final int id) {
        return Philosophers.forks[id].take();
    }

    private void putDownForks() {
        putDownFork(id);
        putDownFork((id + 1) % Philosophers.NUM_PHILOSOPHERS);
    }

    private void putDownFork(final int id) {
        Philosophers.forks[id].release();
    }

    @Override
    public String toString() {
        return "Philosopher " + id;
    }
}

public class Philosophers {
    public static final int NUM_PHILOSOPHERS = 5;
    public static final int UNIT_OF_TIME = 50;
    public static final int RUNNING_SIMULATION_TIME = 60_000;

    public static final Fork[] forks = new Fork[NUM_PHILOSOPHERS];

    static {
        for (int i = 0; i < forks.length; i++)
            forks[i] = new Fork();
    }

    public static void main(final String[] a) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_PHILOSOPHERS; i++)
            threads.add(new Thread(new Philosopher(i)));

        threads.forEach(Thread::start);

        try {
            Thread.sleep(RUNNING_SIMULATION_TIME);
        } catch (final InterruptedException e) {
            System.out.println("Interrupted.");
        }

        threads.forEach(Thread::interrupt);

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
