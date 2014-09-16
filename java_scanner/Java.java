
import java.io.*;
import wci.frontend.*;
import wci.message.*;

/**
 * Runs a Java compiler (well, parser for now).
 * @author The_Almost_Donerz
 */
public class Java
 {
  public static void main(String[] args) throws Exception
   {
    Source s = new Source(new BufferedReader(new FileReader("C:/javatest.in")));
    Parser p = FrontendFactory.createParser("Java", "Top-down", s);
    MessageListener listener = new JavaMessageListener();
    p.addMessageListener(listener);
    s.addMessageListener(listener);
    p.parse();
    
   }
 }
