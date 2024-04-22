package assignement08.exercise01.atomicSolution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class TestWorker implements Runnable {
    private final int id;
    private final static Map<String, Integer> sharedMap = new ConcurrentHashMap<>();
    private int counter = 0;

    public TestWorker(final int id) {
        this.id = id;
    }

    @Override
    public void run() {
        final Random random = new Random();
        final Integer int1 = Integer.valueOf(1);
        final Integer int5 = Integer.valueOf(5);
        final Integer int10 = Integer.valueOf(10);
        int cnt = 10;

        while (--cnt > 0) {
            final String key = getClass().getSimpleName() + random.nextInt(A8Exercise1.NUM_WORKERS);
            updateCounter(random.nextBoolean());

            if (counter == 0) {
                if (sharedMap.containsKey(key) && sharedMap.get(key).equals(int1)) {
                    sharedMap.remove(key);
                    log("{" + key + "} remove 1");
                }
            } else if (counter == 1) {
                if (!sharedMap.containsKey(key)) {
                    sharedMap.put(key, int1);
                    log("{" + key + "} put 1");
                }
            } else if (counter == 5) {
                if (sharedMap.containsKey(key) && sharedMap.get(key).equals(10)) {
                    final Integer prev = sharedMap.put(key, int5);
                    log("{" + key + "} replace " + prev.intValue() + " with 5");
                }
            } else if (counter == 10) {
                if (sharedMap.containsKey(key)) {
                    final Integer prev = sharedMap.put(key, int10);
                    log("{" + key + "} replace " + prev.intValue() + " with 10");
                }
            }
        }
    }

    private void updateCounter(final boolean increment) {
        if (increment) {
            if (++counter > 10)
                counter = 0;
        } else {
            if (--counter < 0)
                counter = 10;
        }
    }

    private void log(final String msg) {
        System.out.println(getClass().getSimpleName() + id + ": " + msg);
    }
}

public class A8Exercise1 {
    static final int NUM_WORKERS = 50;

    public static void main(final String[] args) {
        final List<Thread> allThreads = IntStream.range(0, NUM_WORKERS).boxed()
                .map(TestWorker::new)
                .peek(w -> System.out.println("Starting " + w))
                .map(Thread::new)
                .peek(Thread::start)
                .collect(Collectors.toList());

        // Wait for threads to terminate
        allThreads.forEach(t -> {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
