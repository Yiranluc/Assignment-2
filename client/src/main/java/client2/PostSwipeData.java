package client2;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PostSwipeData {
  private static final int MAX_NUMBER_OF_REQUEST = 500000;
  private static final int NUMBER_OF_REQUESTS_PER_TASK = 5000;
  private static ExecutorService pool;

  public static void main(String[] args)
      throws ExecutionException, InterruptedException, IOException {
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
    System.out.println("ALL DONE");
    System.out.println("Current Time:" + LocalDateTime.now());
    System.out.println("Failed requests: " + CommonVariables.failed_requests.get() +
        " Completed requests: " + CommonVariables.latencyList.size());
    System.out.println("Used "+ number_of_tasks +"threads");
    pool.shutdown();
    writeToCSV();
    System.exit(0);
  }

  public static void writeToCSV() throws IOException {
    FileWriter csvWriter = new FileWriter(CommonVariables.csv_filepath);
    csvWriter.append("startTime,requestType,latency,responseCode" + System.lineSeparator());
    int numberOfRecords = CommonVariables.latencyList.size();
    Collections.sort(CommonVariables.latencyList);
    double min = Long.MAX_VALUE, max = 0, sum = 0;
    for (LatencyRecord record : CommonVariables.latencyList) {
      max = Math.max(max, record.getLatency());
      min = Math.min(min, record.getLatency());
      sum += record.getLatency();
      csvWriter.append(record.toString());
    }
    double mean = sum / (double) numberOfRecords;
    double throughput = (double) numberOfRecords / sum * (double) 1000;
    long median = CommonVariables.latencyList.get((int) (0.5 * CommonVariables.latencyList.size())).getLatency();
    long p99 = CommonVariables.latencyList.get((int) (0.99 * CommonVariables.latencyList.size())).getLatency();
    System.out.println(
        "Mean response time: " + mean + System.lineSeparator()
            + "Median response time: " + median + System.lineSeparator()
            + "Throughput: " + throughput + System.lineSeparator()
            + "99th response time: " + p99 + System.lineSeparator()
            + "Min response time: " + min + System.lineSeparator()
            + "Max response time: " + max
    );
  }

}
