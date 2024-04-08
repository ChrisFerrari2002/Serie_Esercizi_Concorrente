package exercise02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class PalioDiSiena {
    private static final int NUM_JOCKEYS = 10;
    private static int numJockeys = 0;
    private static Lock lock = new ReentrantLock();
    static Condition lastJockeyArrived = lock.newCondition();
    static class Jockeys implements Runnable{
        private int id;

        public Jockeys(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long) (1000 + Math.random() * 501)); // Random arrival time between 1000 and 1500 ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            try {
                numJockeys++;
                System.out.println("Jockey" + id + ": reached starting line");

                if (numJockeys == NUM_JOCKEYS){
                    System.out.println("All Jockeys have arrived!");
                    lastJockeyArrived.signalAll();
                } else {
                    long startTime = System.currentTimeMillis();
                    lastJockeyArrived.await();
                    long waitingTime = System.currentTimeMillis() - startTime;
                    System.out.println("Jockey" + id + " waited " + waitingTime + " ms");
                }



            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }

        }
    }


    public static void main(String[] args) {
        List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            allThreads.add(new Thread(new Jockeys(i)));
        }

        // Start all threads
        allThreads.forEach(Thread::start);

    }
}
