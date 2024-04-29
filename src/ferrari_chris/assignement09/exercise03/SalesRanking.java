package assignement09.exercise03;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class SalesEmployee implements Runnable {
    private static final AtomicInteger salesCounter = new AtomicInteger(0);

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
            int currentSales;
            int newSales;
            do {
                currentSales = salesCounter.get();
                newSales = currentSales + 1 + r.nextInt(5);
            } while (!salesCounter.compareAndSet(currentSales, newSales));

            if (currentSales + newSales > 1000000) {
                running = false;
            }
        }

        System.out.println("Player" + id + ": finished. Rank: " + ranking.getAndIncrement());
    }
}

public class SalesRanking {
    public static void main(final String[] args) {
        final List<Thread> allThread = IntStream.range(0, 10).boxed()
                .map(SalesEmployee::new)
                .map(Thread::new)
                .peek(Thread::start)
                .collect(Collectors.toList());

        for (final Thread t : allThread) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
