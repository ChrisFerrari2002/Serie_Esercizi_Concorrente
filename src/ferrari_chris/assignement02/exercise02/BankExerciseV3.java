package serie2.esercizio02.V3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BankAccount {
    private int amount = 100;
    boolean empty = false;
    public synchronized void withdraw(int userId) {
        Random random = new Random();
        int withdraw = random.nextInt(5, 51);
        if (amount > withdraw) {
            amount -= withdraw;
        } else if (amount > 0){
            withdraw = amount;
            amount = 0;
            empty = true;
        }
        System.out.printf("%d ha prelevato %d dalla banca=%d, nuovo totale=%d\n",
                userId, withdraw, (amount + withdraw), amount);
    }
    public synchronized boolean checkEmpty(){
        return empty;
    }
}

class User implements Runnable {
    private int id;
    private BankAccount bank;
    private Random random = new Random();

    public User(int id, BankAccount bank) {
        this.id = id;
        this.bank = bank;
    }

    @Override
    public void run() {
        while (!bank.checkEmpty()) {
            bank.withdraw(id);
            try {
                Thread.sleep(random.nextLong(5, 21));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

public class BankExerciseV3 {
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
