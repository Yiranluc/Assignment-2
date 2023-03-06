import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReturnLikedPeople implements Runnable {
  private Connection connection;
  private String queueName;

  public ReturnLikedPeople(Connection connection, String queueName) {
    this.connection = connection;
    this.queueName = queueName;
  }

  @Override
  public void run() {
    try {
      final Channel channel = connection.createChannel();
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, "fanout-exchange", "");

      // max one message per receiver
      channel.basicQos(5);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        Gson gson = new Gson();
        Swipe swipe = gson.fromJson(message, Swipe.class);
        // TODO Retrieve data from database based on userid. We fake a retrieved UserLikedPeople object below for now.
        UserLikedPeople userLikes = new UserLikedPeople(swipe.getSwiper(), null);
        System.out.println( "Callback thread ID = " + Thread.currentThread().getId() +
            " Received '" + swipe.toString() + "'" +
            "The likes and dislikes of the current user has swiped: " + userLikes.toString());
      };

      channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    } catch (IOException ex) {
      Logger.getLogger(ConsumerLikedPeople.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
