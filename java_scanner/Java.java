
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
	if(args.length == 0)
		System.out.println("Usage: FILE_PATH\nNo input file was given.");
	else {
		String filePath = args[0];
	    Source s = new Source(new BufferedReader(new FileReader(filePath)));
	    Parser p = FrontendFactory.createParser("Java", "Top-down", s);
	    MessageListener listener = new JavaMessageListener();
	    p.addMessageListener(listener);
	    s.addMessageListener(listener);
	    p.parse();
	}
    
   }
 }
