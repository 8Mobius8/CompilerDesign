
import java.io.*;
import wci.frontend.*;
import wci.message.*;

/**
 * Runs a Java compiler (well, parser for now).
 * @author The_Almost_Donerz
 */
public class Java
{
	private static String USAGE = "Usage: Java compile <FILE_PATH>";
	public static void main(String[] args) throws Exception
	{
		if(args.length != 2 || !args[0].matches("compile")) {
			System.out.println(USAGE);
		} else {
			String filePath = args[1];
			Source s = new Source(new BufferedReader(new FileReader(filePath)));
			Parser p = FrontendFactory.createParser("Java", "Top-down", s);
			MessageListener listener = new JavaMessageListener();
			p.addMessageListener(listener);
			s.addMessageListener(listener);
			p.parse();
		}

	}
}
