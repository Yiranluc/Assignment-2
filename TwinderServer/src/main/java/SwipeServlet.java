import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {

  private static final int NUMBER_OF_THREADS = 200;
  private static final int CHANNEL_NUMBER = 10;
  private RMQChannelPool channelPool;
  private String HOST = "54.201.123.230";
  private Connection connection;
//  private ExecutorService pool;
  private Gson gson;

  private static final int SWIPE_URL_PARTS_LENGTH = 2;
  private static final String LEFT = "left";
  private static final String RIGHT = "right";


  /**
   * Initialize connection and channels.
   */
  @Override
  public void init() {
    try {
      ConnectionFactory connectionFactory = new ConnectionFactory();
      connectionFactory.setHost(HOST);
      connectionFactory.setUsername("user1");
      connectionFactory.setPassword("user1");
      this.connection = connectionFactory.newConnection();
      RMQChannelFactory factory = new RMQChannelFactory(connection);
      this.channelPool = new RMQChannelPool(CHANNEL_NUMBER, factory);
//      this.pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
      this.gson = new Gson();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();

    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) {
    String urlPath = req.getPathInfo();

    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      try {
        res.getWriter().write("missing parameters");
        res.getWriter().flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }

    String[] urlParts = urlPath.split("/");
    Swipe swipe = null;
    try {
      swipe = turnRequestBodytoSwipe(req.getReader());
      Message message = new Message();
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      if (!isUrlValid(urlParts)) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        message.setMessage("Invalid inputs");
      }
      else if (!swipe.isSwipeRequestValid()) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        message.setMessage("User not found");
      } else {
        try {
          Channel channel = this.channelPool.borrowObject();
          channel.exchangeDeclare("fanout-exchange", BuiltinExchangeType.FANOUT, true);
          channel.basicPublish("fanout-exchange", "",
              new AMQP.BasicProperties.Builder()
                  .contentType("application/json")
                  .build(),
              gson.toJson(swipe).getBytes(StandardCharsets.UTF_8));
          this.channelPool.returnObject(channel);
          res.setStatus(HttpServletResponse.SC_CREATED);
          message.setMessage("Write successful");
        } catch (IOException | TimeoutException ex) {
          Logger.getLogger(SwipeServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      System.out.println("Message: " + gson.toJson(message));
      res.getWriter().write(gson.toJson(message));
      res.getWriter().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Swipe turnRequestBodytoSwipe(BufferedReader buffIn) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while( (line = buffIn.readLine()) != null) {
      stringBuilder.append(line);
    }
    return this.gson.fromJson(stringBuilder.toString(), Swipe.class);
  }

  private boolean isUrlValid(String[] urlParts) {
    // urlPath  = "/{leftorright}/"
    // urlParts = [, left or right]
    boolean isUrlValid = urlParts.length == SWIPE_URL_PARTS_LENGTH
        && (urlParts[1].toLowerCase(Locale.ROOT).equals(LEFT) ||
        urlParts[1].toLowerCase(Locale.ROOT).equals(RIGHT));
    return isUrlValid;
  }
}
