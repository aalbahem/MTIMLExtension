package readbiomed.mme.evaluator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 
 * Generate an evaluation confusion matrix
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class Misclassification
{
  private static Pattern p = Pattern.compile("\\|");

  private static Map <String, Set <Integer>> loadBenchmark (String file_name)
  throws IOException
  {
	Map <String, Set <Integer>> benchmark =
			new HashMap <String, Set <Integer>> ();

	BufferedReader b = new BufferedReader(new FileReader(file_name));

	String line;
	
	while ((line = b.readLine()) != null)
	{
	  String [] tokens = p.split(line);
	  
	  Set <Integer> docs = benchmark.get(tokens[1]);

	  if (docs == null)
      {
		docs = new HashSet <Integer> ();
		benchmark.put(tokens[1], docs);
      }

	  docs.add(Integer.parseInt(tokens[0]));
	}

	b.close();
	
	return benchmark;
  }
  
  public static void main (String [] argc)
  throws IOException
  {
    // Load the benchmark file
	Map <String, Set <Integer>> benchmark = loadBenchmark(argc[0]);

    BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

    String line;

    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);

      if (tokens[2].equals("1"))
      {
        int doc = Integer.parseInt(tokens[0]);
        
        for (Map.Entry <String, Set <Integer>> entry : benchmark.entrySet())
        {
    	  if (entry.getValue().contains(doc))
    	  {
    		System.out.println(doc + "|" + entry.getKey() + "|" + tokens[1]);
    	  }
        }	  
      }
    }

    b.close();
  }
}