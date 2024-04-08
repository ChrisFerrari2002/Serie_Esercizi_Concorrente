package ferrari_chris.assignement04.exercise01.V2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class A4Exercise1 {
    final static int[][] matrix = new int[10][10];
    final static int[] rowSum = new int[matrix.length];
    final static int[] colSum = new int[matrix[0].length];
    static int operationsCompleted = 0;

    static final Object conditionThread = new Object();
    static final Object conditionMain = new Object();

    static class Worker implements Runnable{
        private final int id;

        public Worker(int id) {
            this.id = id;
        }
        public static int sumRow(final int row) {
            int result = 0;
            for (int col = 0; col < matrix[row].length; col++)
                result += matrix[row][col];
            operationsCompleted++;
            return result;
        }

        public static int sumColumn(final int row) {
            int temp = 0;
            for (int col = 0; col < matrix.length; col++)
                temp += matrix[col][row];
            operationsCompleted++;
            return temp;
        }

        @Override
        public void run() {
            synchronized (conditionMain) {
                // Compute the sum of rows
                rowSum[id] = sumRow(id);
                if (operationsCompleted == matrix.length)
                    conditionMain.notify();
            }

            synchronized (conditionThread) {

                try {
                    conditionThread.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            synchronized (conditionMain){
                colSum[id] = sumColumn(id);
                if (operationsCompleted == matrix.length)
                    conditionMain.notify();
            }


        }
    }

    public static void main(String[] args) {
        // Initialize matrix with random numbers
        initMatrix();

        // Print matrix
        System.out.println("Matrix:");
        printMatrix();

        List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            allThreads.add(new Thread(new Worker(i)));
        }

        // Start all threads
        allThreads.forEach(Thread::start);

        synchronized (conditionMain) {
            try {
                conditionMain.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Print the sum of rows
            System.out.println("Sum of rows:");
            printArray(rowSum);
        }
        synchronized (conditionThread){
            operationsCompleted = 0;
            conditionThread.notifyAll();
        }



        synchronized (conditionMain){
            try {
                conditionMain.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            // Print the sum of columns
            System.out.println("Sum of columns:");
            printArray(colSum);
        }

        try {
            for (Thread thread : allThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Somma righe: " + Arrays.stream(rowSum).sum());
        System.out.println("Somma colonne: " + Arrays.stream(colSum).sum());
    }



    private static void initMatrix() {
        Random r = new Random();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                matrix[row][col] = 1 + r.nextInt(100);
            }
        }
    }

    private static void printMatrix() {
        for (int i = 0; i < matrix.length; i++)
            printArray(matrix[i]);
    }

    private static void printArray(final int[] array) {
        for (int i = 0; i < array.length; i++)
            System.out.print(array[i] + "\t");
        System.out.println();
    }
}
