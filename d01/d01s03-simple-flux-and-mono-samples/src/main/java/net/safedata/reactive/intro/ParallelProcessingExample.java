package net.safedata.reactive.intro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelProcessingExample {

    public static void main(String[] args) {
        final int availableProcessors = Runtime.getRuntime().availableProcessors() / 2;
        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors); // the used thread-pool

        // the parallel execution class
        ExecutorCompletionService<Integer> executorCompletionService = new ExecutorCompletionService<>(executorService);

        long now = System.currentTimeMillis();
        List<Integer> depositIds = Arrays.asList(35, 74, 834, 34, 83, 65, 383);
        int submittedTasks = 0;
        for (int i = 0; i < depositIds.size(); i++) {
            executorCompletionService.submit(new DepositStockQuery(depositIds.get(i))); // submitting tasks to the executor (/ fork)
            submittedTasks++;
        }

        List<Integer> returnedStocks = new ArrayList<>();
        for (int i = 0; i < submittedTasks; i++) {
            try {
                Future<Integer> future = executorCompletionService.poll(2000, TimeUnit.MILLISECONDS);
                if (future.isDone()) {
                    returnedStocks.add(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Returned stocks: " + returnedStocks);
        System.out.println("The processing took " + (System.currentTimeMillis() - now) + " millis");
        executorService.shutdown();
    }

    private static class DepositStockQuery implements Callable<Integer> {

        private final Random random = new Random(500);

        private final int depositId;

        public DepositStockQuery(int depositId) {
            this.depositId = depositId;
        }

        @Override
        public Integer call() throws Exception {
            long now = System.currentTimeMillis();
            Thread.sleep(random.nextInt(2000));
            System.out.println("\t" + Thread.currentThread().getName()
                    + " for the deposit " + depositId + " took " + (System.currentTimeMillis() - now) + " ms");
            return random.nextInt(1000) * depositId;
        }
    }
}
