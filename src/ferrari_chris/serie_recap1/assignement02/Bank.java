package serie_recap1.assignement02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class WithdrawException extends Exception {
    public WithdrawException(final String message) {
        super(message);
    }
}

interface BankAccount {

    float getBalance();

    void deposit(float amount);

    float withdraw(float requestedAmount) throws WithdrawException;
}

class BasicAccount implements BankAccount {
    private float amount;

    public BasicAccount(float initialAmount) {
        this.amount = initialAmount;
    }

    @Override
    public synchronized float getBalance() {
        return amount;
    }

    public synchronized void deposit(float amount) {
        if (amount < 0)
            return;
        this.amount += amount;
    }

    public synchronized float withdraw(float requestedAmount) throws WithdrawException {
        if (requestedAmount > amount)
            throw new WithdrawException("Insufficient balance");
        amount -= requestedAmount;
        return requestedAmount;
    }

    @Override
    public String toString() {
        return String.format("BasicAccount %s (%.2f$)", hashCode(), getBalance());
    }
}

// FIXME to implement
class SavingsAccount {
    public void payInterest() {
        // FIXME to implement
        // get current balance
        // compute interest (balance * interestRate)
        // add interest to account
    }
}

class Customer implements Runnable {
    final int id;
    final BankAccount account;

    public Customer(int id, BankAccount account) {
        this.id = id;
        this.account = account;
    }

    @Override
    public void run() {
        final float initialBalance = account.getBalance();
        System.out.printf("Customer%d: starting with %.2f$ %n", id, initialBalance);
        float totalWithdrawn = 0;
        float totalDeposit = 0;
        while (!Thread.interrupted()) {

            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(25, 76));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (ThreadLocalRandom.current().nextBoolean()) {
                account.deposit(20);
                totalDeposit += 20;
            } else {
                try {
                    account.withdraw(20);
                    totalWithdrawn += 20;
                } catch (WithdrawException e) {
                    System.out.printf("Customer%d: couldn't withdraw money. %s %n", id, e.getMessage());
                }
            }

        }
        final float finalBalance = account.getBalance();

        System.out.printf("Customer%d: terminating. initialBalance=%.2f finalBalance=%.2f totalDeposit=%.2f totalWithdrawn=%.2f %n", id, initialBalance, finalBalance, totalDeposit, totalWithdrawn);
    }
}

class Bank {
    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        List<Thread> allThreads = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final BankAccount account = new BasicAccount(10_000);
            accounts.add(account);
            allThreads.add(new Thread(new Customer(i, account)));
        }

        allThreads.forEach(Thread::start);

        for (int i = 0; i < 50; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            for (BankAccount account : accounts) {
                if (account instanceof SavingsAccount savingsAccount) {
                    System.out.printf("Paying interest for %s%n", savingsAccount);
                    savingsAccount.payInterest();
                }
            }
        }

        System.out.println("Terminating");
        for (Thread thread : allThreads) {
            try {
                thread.interrupt();
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

