package ferrari_chris.assignement01.exercise02;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

class Worker implements Runnable {
    public void run() {
        for (int i = 0; i < 50; i++) {
            System.out.println(Thread.currentThread().getName() + " running");
            Thread.yield();
        }
    }
}

public class A1Exercise2 {
    private static void cpuIntensiveOps() {
        // Timeout for CPU-intensive operation set to 3 seconds
        final long timeout = System.currentTimeMillis() + 3000; //TimeUnit.SECONDS.toMillis(3)
        final Random r = new Random();
        long count = 0;
        // Loop until timeout
        while (System.currentTimeMillis() < timeout) {
            // CPU intensive operations
            count += r.nextLong(100);
            if (count > 100_000_000)
                count = 0;
        }
    }

    public static void main(String[] args) {
        // Create and start CPU-saturating threads
        final List<Thread> cpuSaturatingThreads = IntStream.range(1, 3 + Runtime.getRuntime().availableProcessors())
                .mapToObj(i -> new Thread(A1Exercise2::cpuIntensiveOps))
                .peek(Thread::start)
                .toList();

        // Create two Worker threads
        Thread thread1 = new Thread(new Worker(), "Thread 1");
        Thread thread2 = new Thread(new Worker(), "Thread 2");

        //thread1.setPriority(Thread.MIN_PRIORITY);
        //thread2.setPriority(Thread.MAX_PRIORITY);




        // Start Worker threads
        thread1.start();
        thread2.start();

        // Wait for Worker threads to terminate
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wait for CPU-saturating threads to terminate
        for (Thread thread : cpuSaturatingThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * Provando a lanciare il programma senza modificarlo, l'ordine è casuale. A volte vengono terminato prima tutto il Thread 1,
 * a volte invece prima il Thread 2 e certe volte vengono mischiati.
 *
 * Mettendo la Max_Priority al Thread2 e la Min_Priority al Thread1, vengono terminati prima tutti i Thread2 e in seguito
 * tutti i Thread1.
 *
 * Inserendo il Thread.yield(), l'output risulta più partizionato. I risultati si alternano tra Thread1 e Thread2 casualmente.
 */
