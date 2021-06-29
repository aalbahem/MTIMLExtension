package readbiomed.mme.util.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Keep only the predicted term as passed as argument
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class PipeMTIFilter
{
  public static void main (String [] argc)
  throws IOException
  {
	Pattern p = Pattern.compile("\\|");

	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

	String line;

	while ( (line = b.readLine()) != null )
	{
	  String [] tokens = p.split(line);

	  if (!tokens[1].equals("MH"))
	  {
		boolean found = false;
		
		for (int i = 2; i < tokens.length; i++)
		{
          if (tokens[i].equals(argc[0]))
          { found = true; break; }
		}

        if (found)
        { System.out.println(tokens[0] + "|" + tokens[1] + "|" + argc[0]); }
	  }
	  else
	  { System.out.println(line); }
	}

	b.close();
  }
}