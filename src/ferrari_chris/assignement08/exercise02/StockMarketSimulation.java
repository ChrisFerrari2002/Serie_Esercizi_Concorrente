package assignement08.exercise02;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Broker implements Runnable {
    private int capital = 100000;
    private final int id;

    Broker(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (!checkIfFinished()) {
            buyShare();
        }
    }

    private void buyShare() {
        int symbolSelected = ThreadLocalRandom.current().nextInt(0, StockMarketSimulation.NUM_OF_STOCKS);
        Stock referredStock = StockMarketSimulation.getStock(symbolSelected);
        double price = referredStock.price();
        if (capital > 0 && capital >= price) {
            System.out.println("Broker " + id + " bought stock " + symbolSelected + " at $" + price);
            capital -= price;
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupted status
        }
    }

    private boolean checkIfFinished() {
        return StockMarketSimulation.finishOperation;
    }

    public int getId() {
        return id;
    }

    public double getCapital() {
        return capital;
    }
}

record Stock(int symbol, int price) {}

class StockMarketSimulation {
    public static final int NUM_OF_STOCKS = 10;
    public static final int NUM_OF_BROKERS = 15;
    public static final int MAX_UPDATES = 10;
    private static final double INITIAL_CAPITAL = 100000.0;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    static volatile boolean finishOperation = false;
    private static final Random random = ThreadLocalRandom.current();
    private static Map<Integer, Stock> stocks;

    private static void updatePrice() {
        lock.writeLock().lock();
        try {
            Map<Integer, Stock> tempMap = new HashMap<>();
            for (int i = 0; i < stocks.size(); i++) {
                Stock stock = stocks.get(i);
                double percentage = random.nextDouble(-25.0, 26.0);
                int newPrice = (int) (stock.price() + stock.price() / 100 * percentage);
                tempMap.put(i, new Stock(stock.symbol(), newPrice));
            }
            stocks = Collections.unmodifiableMap(tempMap);
            System.out.println("Stocks updated:");
            printStocks(tempMap);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static void printStocks(Map<Integer, Stock> tempMap) {
        tempMap.forEach((key, value) -> System.out.println("Stock" + key + ": " + value.price()));
    }

    private static void printBrokersFunds(List<Broker> brokerList) {
        brokerList.forEach(broker -> System.out.println("Broker " + broker.getId() + ": " + broker.getCapital()));
    }

    public static Stock getStock(int symbol) {
        lock.readLock().lock();
        try {
            return stocks.get(symbol);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        initializeStocks();
        List<Thread> threadBrokerList = new ArrayList<>();
        List<Broker> brokerList = new ArrayList<>();
        for (int i = 0; i < NUM_OF_BROKERS; i++) {
            brokerList.add(new Broker(i));
        }
        for (int i = 0; i < NUM_OF_BROKERS; i++) {
            threadBrokerList.add(new Thread(brokerList.get(i)));
        }
        printStocks(stocks);
        threadBrokerList.forEach(Thread::start);

        for (int i = 0; i < MAX_UPDATES; i++) {
            updatePrice();
            printBrokersFunds(brokerList); // Print brokers info after each update

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupted status
            }
        }

        finishOperation = true;

        for (Thread t : threadBrokerList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // Log or handle the exception appropriately
                Thread.currentThread().interrupt(); // Preserve interrupted status
            }
        }
    }

    private static void initializeStocks() {
        Map<Integer, Stock> tempMap = new HashMap<>();
        for (int i = 0; i < NUM_OF_STOCKS; i++) {
            tempMap.put(i, new Stock(i, random.nextInt(100, 500)));
        }
        stocks = Collections.unmodifiableMap(tempMap);
    }
}
