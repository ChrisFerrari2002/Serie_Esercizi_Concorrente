package assignement10.exercise02;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

class Barber implements Runnable {
    private final BarberShop shop;

    public Barber(BarberShop shop) {
        this.shop = shop;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Customer customer = shop.getCustomer();
                if (customer != null) {
                    cutHair(customer);
                } else {
                    goToSleep();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void cutHair(Customer customer) throws InterruptedException {
        System.out.println("Barber is cutting hair of Customer " + customer.getId());
        Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1001));
        System.out.println("Barber finished cutting hair of Customer " + customer.getId());
        Thread.sleep(ThreadLocalRandom.current().nextInt(50, 101));
    }

    private void goToSleep() throws InterruptedException {
        System.out.println("Barber is sleeping");
        synchronized (shop) {
            shop.wait();
        }
    }
}

class Customer implements Runnable {
    private static int idCounter = 1;
    private final int id;
    private final BarberShop shop;

    public Customer(BarberShop shop) {
        this.id = idCounter++;
        this.shop = shop;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(80, 161));
            if (shop.enterShop(this)) {
                System.out.println("Customer " + id + " enters waiting room");
            } else {
                System.out.println("Customer " + id + " left because the waiting room is full");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class BarberShop {
    private final BlockingQueue<Customer> waitingRoom;

    public BarberShop(int waitingRoomSize) {
        this.waitingRoom = new ArrayBlockingQueue<>(waitingRoomSize);
    }

    public synchronized boolean enterShop(Customer customer) {
        if (waitingRoom.offer(customer)) {
            notify();
            return true;
        }
        return false;
    }

    public Customer getCustomer() throws InterruptedException {
        Customer customer = waitingRoom.poll();
        if (customer == null) {
            synchronized (this) {
                while (waitingRoom.isEmpty()) {
                    wait();
                }
                customer = waitingRoom.poll();
            }
        }
        return customer;
    }
}

public class SleepingBarber {
    public static final int WAITING_ROOM_SIZE = 5;
    public static final int SIMULATION_TIME = 30_000;

    public static void main(String[] args) {
        BarberShop shop = new BarberShop(WAITING_ROOM_SIZE);
        Barber barber = new Barber(shop);

        Thread barberThread = new Thread(barber);
        barberThread.start();

        Thread customerGeneratorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Customer customer = new Customer(shop);
                Thread customerThread = new Thread(customer);
                customerThread.start();
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(450, 701));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        customerGeneratorThread.start();

        try {
            Thread.sleep(SIMULATION_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        barberThread.interrupt();
        customerGeneratorThread.interrupt();

        try {
            barberThread.join();
            customerGeneratorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
