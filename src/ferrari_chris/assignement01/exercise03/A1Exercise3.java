package ferrari_chris.assignement01.exercise03;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class A1Exercise3 {
    public static void main(String[] args) {
        // Set the number of desired threads
        int numThreads = 1;

        List<Thread> allThreads = new ArrayList<>();
        // Create and start threads
        for (int i = 0; i < numThreads; i++) {
            final String label = String.format("Process id %s - Thread-%02d: ", ProcessHandle.current().pid(), i + 1);
            Thread thread = new Thread(() -> {
                final Random random = new Random();
                long counter = 0;
                while (true) {
                    for (long l = 0; l < 1_000_000; l++) {
                        counter += random.nextLong(5);
                    }
                    System.out.println(label + "counter: " + counter);
                    if (counter > 100_000_000) {
                        counter = 0;
                        System.out.println(label + "resetting counter");
                    }
                }
            });

            // Start and collect thread
            thread.start();
            allThreads.add(thread);
        }

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
