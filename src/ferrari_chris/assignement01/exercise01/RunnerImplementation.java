package ferrari_chris.assignement01.exercise01;
class FibonacciRunner implements Runnable{

    @Override
    public void run() {
        long fibo1 = 1, fibo2 = 1;
        for (int i = 3; i <= 90; i++) {
            /* Compute fibonacci number */
            long fibonacci = fibo1 + fibo2;

            /* Print result  */
            System.out.printf("Main : fibonacci(%d)=%d %n", i, fibonacci);

            /* Update state for next fibonacci number */
            fibo1 = fibo2;
            fibo2 = fibonacci;
        }
    }
}
public class RunnerImplementation {
    public static void main(String[] args) {
        final int numThreads = 5;
        final FibonacciRunner[] runners = new FibonacciRunner[5];
        final Thread[] threads = new Thread[5];
        for (int i = 0; i < numThreads; i++) {
            runners[i] = new FibonacciRunner();
            threads[i] = new Thread(runners[i]);
            threads[i].start();
        }
    }
}
