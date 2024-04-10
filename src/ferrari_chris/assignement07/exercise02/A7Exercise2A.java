

import java.util.concurrent.ThreadLocalRandom;

// REMARK: do not modify this class !
class SharedState {
    private int count = 0;
    private int value = 0;

    public SharedState() {
        System.out.println("SharedState");
    }

    public void increment(int delta) {
        value += delta;
        count += 1;
    }

    public int getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public float getAverage() {
        return count == 0 ? 0 : (float) value / (float) count;
    }
}

public class A7Exercise2A {

    static class Helper implements Runnable {
        @Override
        public void run() {
            System.out.println("Helper : started and waiting until shared state is set!");
            // REMARK: busy wait
            while (true) {
                if (A7Exercise2A.sharedState != null)
                    break;
            }

            int lastValue = A7Exercise2A.sharedState.getValue();

            System.out.println("Helper : shared state initialized and current value is " + lastValue + ". Waiting until value changes");

            // Wait until value changes
            while (true) {
                final int curValue = A7Exercise2A.sharedState.getValue();
                if (lastValue != curValue) {
                    lastValue = curValue;
                    break;
                }
            }
            System.out.println("Helper : value changed to " + lastValue + "!");

            for (int i = 0; i < 5000; i++) {
                A7Exercise2A.sharedState.increment(ThreadLocalRandom.current().nextInt(1, 10));
                if ((i % 100) == 0)
                    try {
                        Thread.sleep(1);
                    } catch (final InterruptedException e) {
                        System.err.println("Helper interrupted.");
                        return;
                    }
            }
            System.out.println("Helper : completed");
        }
    }

    static class Starter implements Runnable {

        @Override
        public void run() {
            System.out.println("Starter: sleeping");
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                System.err.println("Starter interrupted.");
                return;
            }

            System.out.println("Starter: initialized shared state");
            A7Exercise2A.sharedState = new SharedState();

            // Sleep before updating
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                System.err.println("Starter interrupted.");
                return;
            }

            // Perform 5000 increments and exit
            System.out.println("Starter: begin incrementing");
            for (int i = 0; i < 5000; i++) {
                A7Exercise2A.sharedState.increment(ThreadLocalRandom.current().nextInt(1, 10));
                if ((i % 100) == 0)
                    try {
                        Thread.sleep(1);
                    } catch (final InterruptedException e) {
                        System.err.println("Starter interrupted.");
                        return;
                    }
            }
            System.out.println("Starter: completed");
        }
    }

    static SharedState sharedState = null;

    public static void main(final String[] args) {
        // Create Threads
        final Thread readThread = new Thread(new Helper());
        final Thread updateThread = new Thread(new Starter());

        // Start Threads
        readThread.start();
        updateThread.start();

        // Wait until threads finish
        try {
            updateThread.join();
            readThread.join();
        } catch (final InterruptedException e) {
            System.err.println("Main interrupted.");
        }
        System.out.println(String.format("Main final results: value=%d count=%d average=%.2f"
                , A7Exercise2A.sharedState.getValue()
                , A7Exercise2A.sharedState.getCount()
                , A7Exercise2A.sharedState.getAverage())
        );
    }
}
