package assignement09.exercise01;

import ferrari_chris.assignement04.exercise01.V1.A4Exercise1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Phaser;

class Calculator implements Runnable{
    private final int[][] matrix;
    private final int id;

    public Calculator(int[][] matrix, int id) {
        this.matrix = matrix;
        this.id = id;
    }

    @Override
    public void run() {
        computeSumOfRow();
        MatrixOperations.phaser.arriveAndAwaitAdvance();
        computeSumOfCol();
        MatrixOperations.phaser.arriveAndAwaitAdvance();
    }

    private void computeSumOfRow(){
        int[] row = matrix[id];
        int sum = 0;
        for (int i : row) {
            sum += i;
        }
        MatrixOperations.rowSum[id] = sum;
    }
    private void computeSumOfCol(){

        int sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            sum += matrix[i][id];
        }
        MatrixOperations.colSum[id] = sum;
    }
}

class MatrixOperations {
    private final static int MATRIX_SIZE = 10;
    final static int[][] matrix = new int[10][10];
    final static int[] rowSum = new int[matrix.length];
    final static int[] colSum = new int[matrix[0].length];
    final static Phaser phaser = new Phaser(MATRIX_SIZE);

    private static void initMatrix() {
        Random r = new Random();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                matrix[row][col] = 1 + r.nextInt(100);
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
        for (int i = 0; i < MATRIX_SIZE; i++) {
            allThreads.add(new Thread(new Calculator(matrix, i)));
        }

        // Start all threads
        allThreads.forEach(Thread::start);

        //SUM OF ROWS
        phaser.awaitAdvance(phaser.getPhase());

        // Print the sum of rows
        System.out.println("Sum of rows:");
        printArray(rowSum);

        // Compute sum of columns
        phaser.awaitAdvance(phaser.getPhase());

        // Print the sum of columns
        System.out.println("Sum of columns:");
        printArray(colSum);
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
