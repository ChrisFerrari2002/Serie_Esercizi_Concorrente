package ferrari_chris.assignement01.exercise01;

public class LambdaImplementation {
    public static void main(String[] args) {
        final int numThreads = 5;
        final Thread[] threads = new Thread[5];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                long fibo1 = 1, fibo2 = 1;
                for (int j = 3; j <= 90; j++) {
                    /* Compute fibonacci number */
                    long fibonacci = fibo1 + fibo2;

                    /* Print result  */
                    System.out.printf("Main : fibonacci(%d)=%d %n", j, fibonacci);

                    /* Update state for next fibonacci number */
                    fibo1 = fibo2;
                    fibo2 = fibonacci;
                }
            });
            threads[i].start();
        };
    }
}
