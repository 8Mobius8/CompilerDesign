
import wci.message.Message;
import wci.message.MessageListener;

/**
 *
 * @author Jason Hungerford
 */
public class JavaMessageListener implements MessageListener
 {

  @Override
  public void messageReceived(Message m)
   {
    System.out.println(m.toString());
   }
 }
