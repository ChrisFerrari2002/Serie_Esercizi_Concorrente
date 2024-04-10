

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Event {
    private final long num;

    public Event(final long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Event: " + num;
    }
}

class EventSource implements Runnable {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, EventListener> allListeners = new HashMap<>();

    @Override
    public void run() {
        for (long i = 0; i < 30_000_000; i++) {
            // Create a new event
            final Event e = new Event(i);

            // avoid concurrent access to the map
            lock.lock();
            try {
                // Handle the event for each eventListener that has registered to this EventSource
                for (final int id : allListeners.keySet()) {
                    final EventListener listener = allListeners.get(id);
                    listener.onEvent(id, e);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void registerListener(final int id, final EventListener listener) {
        // avoid concurrent access to the map
        lock.lock();
        try {
            allListeners.put(id, listener);
        } finally {
            lock.unlock();
        }
    }
}

class EventListener {
    private final int id;

    public EventListener(final int id, final EventSource eventSource) {
        // Add listener to the eventSource to get event notifications
        eventSource.registerListener(id, this);

        // Sleep added to facilitate the appearance of the problem. In a real world
        // program, other initialization operations may be performed
        try {
            Thread.sleep(4);
        } catch (final InterruptedException e) {
            // Thread interrupted
        }

        this.id = id;
    }

    public void onEvent(final int listenerID, final Event e) {
        // Check that the listener's ID called from the eventSource matches the listener's instance's is
        if (listenerID != id)
            System.out.println("Inconsistent listener ID" + listenerID + " : " + e);
    }
}

public class EventSimulation {
    public static void main(final String[] args) {
        final EventSource eventSource = new EventSource();
        final Thread eventSourceThread = new Thread(eventSource);

        // Start eventSource thread
        eventSourceThread.start();

        // Create and register listeners to eventSource
        final List<EventListener> allListeners = new ArrayList<>();
        for (int i = 1; i <= 20; i++)
            allListeners.add(new EventListener(i, eventSource));

        // Wait for thread to terminate
        try {
            eventSourceThread.join();
        } catch (final InterruptedException e) {
            // Thread interrupted
        }
    }
}
