package client1;

import client2.LatencyRecord;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonVariables {
  public static AtomicInteger number_of_requests = new AtomicInteger(0);
  public static AtomicInteger failed_requests = new AtomicInteger(0);

}
