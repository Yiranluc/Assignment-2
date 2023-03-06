package client2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.ThreadLocalRandom;

public class PostSwipeDataTask implements Runnable {

  private final static int COMMENT_LENGTH = 256;
  private final static String ALPHABETIC_NUMERIC_APOSTRIPHIC_STRING =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
          + "0123456789"
          + "abcdefghijklmnopqrstuvxyz"
          + ",.:;!?()''";
  private int number_of_requests;

  private final ApiClient apiClient;
  private final SwipeApi swipeApi;

  public PostSwipeDataTask(int number_of_requests) {
    this.number_of_requests = number_of_requests;
    this.apiClient = new ApiClient();
    apiClient
        .setBasePath("http://servletbalancer-1303776882.us-west-2.elb.amazonaws.com/Server_war");
//      apiClient.setBasePath("http://54.201.123.230:8080/Server_war");
//    apiClient.setBasePath("http://localhost:8080/TwinderServer_war_exploded");
    this.swipeApi = new SwipeApi(apiClient);
  }

  @Override
  public void run() {
    while (number_of_requests > 0) {
      int i = 0;
      while (i < 5) {
        try {
          ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
          SwipeDetails body = new SwipeDetails();
          body.setSwiper(String.valueOf(threadLocalRandom.nextInt(1, 5001)));
          body.setSwipee(String.valueOf(threadLocalRandom.nextInt(1, 1000001)));
          StringBuilder stringBuilder = new StringBuilder();
          for (int j = 0; j < COMMENT_LENGTH; j++) {
            stringBuilder.append(ALPHABETIC_NUMERIC_APOSTRIPHIC_STRING
                .charAt(
                    threadLocalRandom.nextInt(0, ALPHABETIC_NUMERIC_APOSTRIPHIC_STRING.length())));
          }
          body.setComment(stringBuilder.toString());
          long before_post_timestamp = System.currentTimeMillis();
          ApiResponse<Void> response = swipeApi
              .swipeWithHttpInfo(body, threadLocalRandom.nextBoolean() ? "left" : "right");
          long after_post_timestamp = System.currentTimeMillis();
          CommonVariables.latencyList.add(new LatencyRecord(before_post_timestamp, "Post",
              after_post_timestamp - before_post_timestamp, response.getStatusCode()));
//          if(CommonVariables.latencyList.size() % 50000 == 0) {
          System.out.println(CommonVariables.latencyList.size());
//          }
          number_of_requests--;
          break;
        } catch (ApiException e) {
          System.out.println(e);
          i++;
          try {
            Thread.sleep(50);
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }
          if (i == 5) {
            CommonVariables.failed_requests.getAndIncrement();
            break;
          }
        }
      }
    }
  }
}
