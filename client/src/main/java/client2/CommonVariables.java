package client2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonVariables {
  public static List<LatencyRecord> latencyList = new CopyOnWriteArrayList<>();
  public static AtomicInteger failed_requests = new AtomicInteger(0);
  public static String csv_filepath = "latencyRecord.csv";

}
