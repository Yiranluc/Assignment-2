import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostTask implements Runnable{
  private HttpServletRequest req;
  private HttpServletResponse res;
  private Gson gson;
  private RMQChannelPool channelPool;

  private static final int SWIPE_URL_PARTS_LENGTH = 2;
  private static final String LEFT = "left";
  private static final String RIGHT = "right";

  public PostTask(HttpServletRequest req, HttpServletResponse res, RMQChannelPool channelPool) {
    this.req = req;
    this.res = res;
    this.gson = new Gson();
    this.channelPool = channelPool;
  }

  @Override
  public void run() {
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
          Gson gson = new Gson();
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

//    this.connection.close();
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
