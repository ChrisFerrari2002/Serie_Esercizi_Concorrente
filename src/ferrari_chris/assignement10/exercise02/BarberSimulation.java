package assignement10.exercise02;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

class Barber implements Runnable{
    private boolean isSleeping = true;
    private final Random random = ThreadLocalRandom.current();

    public void cutHair(){
        int cutTime = random.nextInt(500, 1001);
        try {
            System.out.println("Cutting hairs");
            Thread.sleep(cutTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized boolean isSleeping(){
        return isSleeping;
    }


    @Override
    public void run() {

    }
}
final class Customer implements Runnable{
    private final int id;
    private final Barber barber;

    public Customer(int id, Barber barber) {
        this.id = id;
        this.barber = barber;
    }


    @Override
    public void run() {

    }
}
class Saloon{
    private boolean chairIsOccupied = true;
    private final Barber barber;
    public static ConcurrentLinkedQueue<Customer> waitingRoom = new ConcurrentLinkedQueue<>();
    private int dailyCustomer = 0;

    public Saloon(Barber barber) {
        this.barber = barber;
    }



}

public class BarberSimulation {
    public static void main(String[] args) {

    }
}
