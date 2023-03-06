import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConsumerLikesDislikes {

  private final static String QUEUE_NAME = "fanout-likes/dislikes";
  private static final int NUMBER_OF_THREADS = 300;
  private static final String HOST = "54.149.108.74";

  //Given a user id, return the number of likes and dislikes this user has swiped
  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    factory.setUsername("user1");
    factory.setPassword("user1");
    final Connection connection = factory.newConnection();

    ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    for (int t = 0; t < NUMBER_OF_THREADS; t++) {
      pool.submit(new ReturnUserLikesDislikes(connection, QUEUE_NAME));
    }

  }
}