package client2;

public class LatencyRecord implements Comparable<LatencyRecord>{
  private long startTime;
  private String requestType;
  private long latency;
  private int responseCode;

  public LatencyRecord(long startTime, String requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public long getLatency() {
    return latency;
  }

  @Override
  public String toString() {
    return startTime +"," + requestType + "," + latency +
        "," + responseCode + System.lineSeparator();
  }

  @Override
  public int compareTo(LatencyRecord o) {
    return (int) (this.latency - o.latency);
  }
}
