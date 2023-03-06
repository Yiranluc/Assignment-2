import io.swagger.client.ApiClient;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import org.junit.Test;

public class MultithreadedClientTest {

  @Test
  public void swipeTest() throws Exception {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("http://localhost:8080/TwinderServer_war_exploded");
    SwipeApi api = new SwipeApi(apiClient);

    SwipeDetails body = new SwipeDetails();
    body.setSwipee("123");
    body.setSwiper("123");
    body.setComment("you loser");
    String leftorright = "left";
//    api.swipe(body, leftorright);
    // TODO: test validations
  }
}
