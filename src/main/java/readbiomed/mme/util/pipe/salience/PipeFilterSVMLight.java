package readbiomed.mme.util.pipe.salience;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipeFilterSVMLight
{
  private static final Pattern p = Pattern.compile("\\|");

  private static final Pattern p_extract = Pattern.compile("^(C[0-9]+)\\(([0-9]+\\.[0-9]+)\\)");
  
  public static void main (String [] argc)
  throws IOException
  {
	Map <String, Set <String>> benchmark =
			new HashMap <String, Set <String>> ();
	
	// Read the MHs for each citation
    {
      BufferedReader b = new BufferedReader(new FileReader(argc[0]));

      String line;

      while ((line = b.readLine()) != null)
      {
        String [] tokens = p.split(line);

        Set <String> mhs = benchmark.get(tokens[0]);

        if (mhs == null)
        {
          mhs = new HashSet <String> ();
          benchmark.put(tokens[0], mhs);
        }

        mhs.add(tokens[1]);
      }

      b.close();
    }
  
    {
      Map <String, Integer> trie =
    		  new HashMap <String, Integer> ();
    	
      BufferedReader b = new BufferedReader(new FileReader(argc[1]));
      
      BufferedWriter w_train = new BufferedWriter(new FileWriter(argc[3] + ".train"));
      BufferedWriter w_test = new BufferedWriter(new FileWriter(argc[3] + ".test"));
      
      Double filter_threshold = new Double(argc[4]);

      String line;

      int count = 0;
      while ((line = b.readLine()) != null)
      {
    	count++;
        String [] tokens = p.split(line);

        if (benchmark.get(tokens[0]) == null)
        { continue; }

        Map <Integer, Double> token_score =
        		new TreeMap <Integer, Double> ();

        for (int i = 2; i < tokens.length; i++)
        {
          Matcher m = p_extract.matcher(tokens[i]);

          if (m.find())
          {
            String cui = m.group(1);
            Double weight = new Double(m.group(2));

            if (weight > filter_threshold)
            {
              // Get concept id, add it if it does not exist
              Integer token_id = trie.get(cui);

              if (token_id == null)
              {
                token_id = trie.size() + 1;
                trie.put(cui, token_id);
              }

              token_score.put(token_id, weight);
            }
          }
        }

        BufferedWriter w;

        if (count % 3 == 0)
        { w = w_test; }
        else
        { w = w_train; }

        // Print the line
        //    Check the class
        if (benchmark.get(tokens[0]).contains(argc[2]))
        { w.write("+1"); }
        else
        { w.write("-1"); }    

        //    Print the features
        for (Map.Entry <Integer, Double> entry : token_score.entrySet())
        {
          w.write(" ");
          w.write(entry.getKey().toString());
          w.write(":1");
        }

        w.newLine();
      }

      System.out.println(count);
      b.close();
      w_train.close();
      w_test.close();
    }
  }
}