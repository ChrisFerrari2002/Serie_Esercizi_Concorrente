package assignement11.exercise02;

import java.util.Random;
import java.util.concurrent.*;

class Multiplier implements Callable<Integer> {
    private final int matrixSize;
    private final int[][] m0;
    private final int[][] m1;
    private final int[][] m2;

    public Multiplier(int matrixSize, int[][] m0, int[][] m1) {
        this.matrixSize = matrixSize;
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = new int[matrixSize][matrixSize];
    }

    @Override
    public Integer call() {
        // Multiply matrix
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    m2[i][j] += m0[i][k] * m1[k][j];
                }
            }
        }
        return sumMatrix(m2);
    }

    private int sumMatrix(int[][] matrix) {
        int sum = 0;
        for (int[] row : matrix) {
            for (int element : row) {
                sum += element;
            }
        }
        return sum;
    }
}

public class MatrixMultiplication {
    public static final int NUM_OPERATIONS = 100_000;
    public static final int MATRIX_SIZE = 64;

    public static void main(final String[] args) {
        final ExecutorService executor = Executors.newFixedThreadPool(16);
        final CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        final Random rand = new Random();

        System.out.println("Simulation started");
        for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
            int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
            int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];

            // Initialize with random values
            for (int i = 0; i < MATRIX_SIZE; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    m0[i][j] = rand.nextInt(10);
                    m1[i][j] = rand.nextInt(10);
                }
            }
            completionService.submit(new Multiplier(MATRIX_SIZE, m0, m1));
        }
        executor.shutdown();

        try {
            int largestValue = 0;
            for (int t = 0; t < NUM_OPERATIONS; t++) {
                final Future<Integer> future = completionService.take();
                int value = future.get();
                if (value > largestValue) {
                    largestValue = value;
                }
            }
            System.out.println("Largest sum of matrix elements: " + largestValue);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        System.out.println("Simulation completed");
    }
}
