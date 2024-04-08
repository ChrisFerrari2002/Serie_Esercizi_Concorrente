package ferrari_chris.assignement03.exercise02.volatileVar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Sensor implements Runnable {
    private final int threshold;

    public Sensor(final int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void run() {
        System.out.println("Sensor[" + threshold + "]: start monitoring!");

        while (!A3Exercise2.resetIfAbove(threshold)) {
            /* Busy wait */
        }

        System.out.println("Sensor[" + threshold + "]: threshold passed!");
    }
}

public class A3Exercise2 {
    private static volatile int amount = 0;

    static int incrementAndGet(final int step) {
        amount += step;
        return amount;
    }

    static boolean resetIfAbove(final int threshold) {
        if (amount > threshold) {
            amount = 0;
            return true;
        }
        return false;
    }

    public static void main(final String[] args) {
        final List<Thread> threads = new ArrayList<>();

        // Create threads and sensors
        for (int i = 1; i <= 10; i++) {
            final int sensorThreshold = (i * 10);
            threads.add(new Thread(new Sensor(sensorThreshold)));
        }

        // start all threads
        threads.forEach(Thread::start);

        final Random random = new Random();
        while (true) {
            final int increment = random.nextInt(1, 9);
            final int newAmount = incrementAndGet(increment);
            System.out.println("Actuator: shared state incremented to " + newAmount + " Increment: " + increment);
            if (newAmount > 120)
                break;
            try {
                Thread.sleep(random.nextLong(5, 11));
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (final Thread t : threads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}