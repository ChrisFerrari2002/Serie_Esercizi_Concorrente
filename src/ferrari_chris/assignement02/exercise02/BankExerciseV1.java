package serie2.esercizio02.V1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount{
    Lock lock = new ReentrantLock();
    int amount = 100;
    boolean empty = false;
}
class User implements Runnable{
    private int id;
    private BankAccount bank = null;

    public User(int id, BankAccount bank) {
        this.id = id;
        this.bank = bank;
    }

    @Override
    public void run() {
        int withdraw;
        Random random = new Random();
        bank.lock.lock();
        try {
            while (!bank.empty){
                withdraw = random.nextInt(5, 51);

                if (bank.amount > withdraw){
                    bank.amount -= withdraw;
                } else {
                    withdraw = bank.amount;
                    bank.amount = 0;
                    bank.empty = true;
                }
                System.out.printf("%d ha prelevato %d dalla banca=%d, nuovo totale=%d\n",
                        id, withdraw, (bank.amount + withdraw), bank.amount);
                bank.lock.unlock();
                final long duration = random.nextLong(5, 21);
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bank.lock.lock();
            }
        } finally {
            bank.lock.unlock();
        }
    }
}

public class BankExerciseV1 {
    public static void main(String[] args) {
        BankAccount bank = new BankAccount();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            threads.add(new Thread(new User(i, bank)));
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
