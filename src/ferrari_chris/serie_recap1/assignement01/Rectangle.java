package serie_recap1.assignement01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Rectangle {
    private final int x1;
    private final int y1;
    private int x2;
    private int y2;

    public Rectangle(final int newX1, final int newY1, final int newX2, final int newY2) {
        x1 = newX1;
        y1 = newY1;
        x2 = newX2;
        y2 = newY2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public void setX2(final int newX2) {
        this.x2 = newX2;
    }

    public void setY2(final int newY2) {
        this.y2 = newY2;
    }

    @Override
    public String toString() {
        return "[" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + "]";
    }
}

class Resizer implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(random.nextInt(3) + 2);
            } catch (final InterruptedException e) {
            }

            // generate variation for point between -2 e 2
            final int deltaX2 = random.nextInt(5) - 2;
            final int deltaY2 = random.nextInt(5) - 2;

            // campute new coordinates x2 e y2
            final int newX2 = RectangleTest.rect.getX2() + deltaX2;
            final int newY2 = RectangleTest.rect.getY2() + deltaY2;

            final boolean isLine = (RectangleTest.rect.getX1() == newX2) || (RectangleTest.rect.getY1() == newY2);
            final boolean isPoint = (RectangleTest.rect.getX1() == newX2) && (RectangleTest.rect.getY1() == newY2);
            final boolean isNegative = (RectangleTest.rect.getX1() > newX2) || (RectangleTest.rect.getY1() > newY2);

            // Verify that the change doesn't change the rectangle to a line or dot.
            if (!isLine && !isPoint && !isNegative) {
                RectangleTest.rect.setX2(newX2);
                RectangleTest.rect.setY2(newY2);

                if (newX2 != RectangleTest.rect.getX2() || newY2 != RectangleTest.rect.getY2())
                    System.out.println("ERROR - NewX2: " + newX2 + ", NewY2: " + newY2 + ", " + RectangleTest.rect);
            }
        }
    }
}

/**
 * Program that continuously simulates the variation of the rectangle's dimensions
 */
class RectangleTest {
    static Rectangle rect = new Rectangle(10, 10, 20, 20);

    public static void main(final String[] args) {
        final List<Thread> allThreads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++)
            allThreads.add(new Thread(new Resizer()));

        System.out.println("Simulation started");
        for (final Thread t : allThreads) {
            t.start();
        }

        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Simulation finished");
    }
}