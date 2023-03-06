package client1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PostSwipeData {
  // Test run is with 10000 requests and 1 thread.
  private static final int MAX_NUMBER_OF_REQUEST = 500000;
  private static final int NUMBER_OF_REQUESTS_PER_TASK = 10000;
  private static ExecutorService pool;

  public static void main(String[] args)
      throws ExecutionException, InterruptedException {
    long start_time = System.currentTimeMillis();

    int number_of_tasks = MAX_NUMBER_OF_REQUEST / NUMBER_OF_REQUESTS_PER_TASK;
    int number_of_requests_for_last_task = MAX_NUMBER_OF_REQUEST % number_of_tasks;
    number_of_tasks = (number_of_requests_for_last_task < NUMBER_OF_REQUESTS_PER_TASK) ?
        number_of_tasks + 1 : number_of_tasks;

    pool = Executors.newFixedThreadPool(number_of_tasks);
    List<Future<?>> futures = new ArrayList<Future<?>>();

    for (int t = 0; t < number_of_tasks - 1; t++) {
      futures.add(pool.submit(new PostSwipeDataTask(NUMBER_OF_REQUESTS_PER_TASK)));
    }
    futures.add(pool.submit(new PostSwipeDataTask(number_of_requests_for_last_task)));

    for (Future<?> future : futures) {
      future.get();
    }
    long end_time = System.currentTimeMillis();
    System.out.println("ALL DONE");
    System.out.println("Failed requests: " + CommonVariables.failed_requests.get() +
        " Completed requests: " + CommonVariables.number_of_requests.get());
    double total_time = (double) ((end_time - start_time) / 1000);
    double throughput = (double) CommonVariables.number_of_requests.get() / total_time;
    System.out.println("Total time spent: " + total_time + "s; Total throughput: "
        + throughput + " requests/second");
    pool.shutdown();
    System.exit(0);
  }
}
