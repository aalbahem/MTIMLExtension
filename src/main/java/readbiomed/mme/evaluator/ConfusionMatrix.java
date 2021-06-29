package readbiomed.mme.evaluator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * 
 * Generate an evaluation confusion matrix
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class ConfusionMatrix
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
	//Map <String, Set <Integer>> benchmark = loadBenchmark(argc[0]);
	Map <String, Set <Integer>> benchmark = loadBenchmark("C:\\datasets\\DSTO\\ARFF\\benchmark.txt");

    Map <String, Map <String, Integer>> cm =
			  new TreeMap <String, Map <String, Integer>> ();

    //BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader b = new BufferedReader(new FileReader("C:\\datasets\\DSTO\\ARFF\\annotation.txt"));

    Set <String> categories = new TreeSet <String> (benchmark.keySet());
    
    String line;

    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);

      if (tokens[2].equals("1"))
      {
        int doc = Integer.parseInt(tokens[0]);
        
        categories.add(tokens[1]);
      
        for (Map.Entry <String, Set <Integer>> entry : benchmark.entrySet())
        {
    	  if (entry.getValue().contains(doc))
    	  {
    		Map <String, Integer> count = cm.get(entry.getKey());

    		if (count == null)
    		{
    		  count = new TreeMap <String, Integer> ();
    		  cm.put(entry.getKey(), count);
    		}
    		
    		if (count.get(tokens[1]) == null)
    		{ count.put(tokens[1],  1); }
    		else
    		{ count.put(tokens[1], count.get(tokens[1]) + 1); }
    	  }
        }	  
      }
    }

    b.close();

    // Print cm
    System.out.print("|");
    for (String c : categories)
    {
      System.out.print(c);
      System.out.print("|");
    }
    System.out.println();
    
    for (String c : categories)
    {
      System.out.print(c);
      System.out.print("|");

      for (String c1 : categories)
      {
    	if (cm.get(c) != null && cm.get(c).get(c1) != null)
    	{
   	      System.out.print(cm.get(c).get(c1));
   	      System.out.print("|");
    	}
    	else
    	{ System.out.print("0|"); }
      }
      System.out.println();
    }
  }
}