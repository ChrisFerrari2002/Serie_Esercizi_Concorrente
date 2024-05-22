package assignement11.exercise01;

import java.util.Random;
import java.util.concurrent.*;

class Multiplier implements Callable<int[][]> {
    private int matrixSize = 0;
    private final int[][] m0;
    private final int[][] m1;

    private final int[][] m2 = new int[matrixSize][matrixSize];

    public Multiplier(int matrixSize, int[][] m0, int[][] m1) {
        this.matrixSize = matrixSize;
        this.m0 = m0;
        this.m1 = m1;
    }

    @Override
    public int[][] call() throws Exception {
        // Multiply matrix
        for (int i = 0; i < m0[0].length; i++)
            for (int j = 0; j < m1.length; j++)
                for (int k = 0; k < m0.length; k++)
                    m2[i][j] += m0[i][k] * m1[k][j];

        return m2;
    }
}

public class MatrixMultiplication {
    public static final int NUM_OPERATIONS = 100_000;
    public static final int MATRIX_SIZE = 64;

    public static void main(final String[] args) {
        final ExecutorService executor = Executors.newFixedThreadPool(16);
        final Random rand = new Random();

        int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
        int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];

        System.out.println("Simulation started");
        for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
            // Initialize with random values
            for (int i = 0; i < MATRIX_SIZE; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    m0[i][j] = rand.nextInt(10);
                    m1[i][j] = rand.nextInt(10);
                }
            }
            executor.submit(new Multiplier(MATRIX_SIZE, m0, m1));
        }

        executor.shutdown();

        System.out.println("Simulation completed");
    }
}
