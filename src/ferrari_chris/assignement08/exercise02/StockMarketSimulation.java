package assignement08.exercise02;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class Broker implements Runnable{
    private final Random random = new Random();
    private static int capital = 100000;

    @Override
    public void run() {
        while (!StockMarketSimulation.finishOperation){
            if (capital )
        }
    }
    private void buyShare(){
        int symbolSelected = ThreadLocalRandom.current().nextInt(0, StockMarketSimulation.NUM_OF_STOCKS);

        try {
            Thread.sleep(5);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

record Stock(Integer symbol, double price){};

class StockMarketSimulation {
    public static final int NUM_OF_STOCKS = 10;
    public static final int NUM_OF_BROKERS = 15;
    public static final int MAX_UPDATES = 1000;
    static volatile Map<Integer, Stock> stocks = null;
    static volatile boolean finishOperation = false;
    private static final Random random = new Random();

    private static void updatePrice(){
        random.nextDouble(100.0, 500.0);
        Map<Integer, Stock> tempMap = new HashMap<>();
        double percentage = random.nextDouble(-25.0, 26);
        for (int i = 0; i < stocks.size(); i++) {
            double newPrice = stocks.get(i).price() + stocks.get(i).price() / 100 * percentage;
            tempMap.put(i, new Stock(stocks.get(i).symbol(), newPrice));
        }
        stocks = Collections.unmodifiableMap(tempMap);
    }
    public static void main(String[] args) {
        Map<Integer, Stock> tempMap = new HashMap<>();
        List<Thread> brokerList = new ArrayList<>();

        for (int i = 0; i < NUM_OF_STOCKS; i++) {
            tempMap.put(i, new Stock(i, random.nextDouble(100.0, 500.0)));
        }
        stocks = Collections.unmodifiableMap(tempMap);

        for (int i = 0; i < NUM_OF_BROKERS; i++) {
            brokerList.add(new Thread(new Broker()));
        }
        brokerList.forEach(Thread::start);

        for (int i = 0; i < MAX_UPDATES; i++) {
            updatePrice();

            try {
                Thread.sleep(15);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        finishOperation = true;

        for (final Thread t : brokerList)
            try {
                t.join();
            } catch (final InterruptedException e) {
                // Do nothing
            }
    }
}
