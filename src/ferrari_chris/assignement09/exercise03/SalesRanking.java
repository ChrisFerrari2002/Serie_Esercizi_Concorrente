

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class SalesEmployee implements Runnable {
    private static final Lock lock = new ReentrantLock();
    private static int salesCounter = 0;

    final static AtomicInteger ranking = new AtomicInteger(1);
    final private int id;

    public SalesEmployee(final int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Player" + id + ": starting");
        final Random r = new Random();

        boolean running = true;
        while (running) {
            lock.lock();
            try {
                if (salesCounter > 1000000) {
                    salesCounter = 0;
                    running = false;
                } else {
                    salesCounter += 1 + r.nextInt(5);
                }
            } finally {
                lock.unlock();
            }
        }

        // Remark: just for visualization purposes.
        System.out.println("Player" + id + ": finished. Rank: " + ranking.getAndIncrement());
    }
}

public class SalesRanking {
    public static void main(final String[] args) {
        final List<Thread> allThread = IntStream.range(0, 10).boxed()
                .map(SalesEmployee::new)     // Create player
                .map(Thread::new)     // Create thread for player (same as new Thread(player)
                .peek(Thread::start)  // Call start on thread
                .collect(Collectors.toList());    // Collect threads

        for (final Thread t : allThread) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
