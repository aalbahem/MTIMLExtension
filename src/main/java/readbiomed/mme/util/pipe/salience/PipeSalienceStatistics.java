package readbiomed.mme.util.pipe.salience;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipeSalienceStatistics
{
  private static final Pattern p = Pattern.compile("\\|");

  private static final Pattern p_extract = Pattern.compile("^C[0-9]+\\(([0-9]+\\.[0-9]+)\\)");
  
  public static void main (String [] argc)
  throws IOException
  {
    BufferedReader b = new BufferedReader(new FileReader(argc[0]));

    String line;
    
    Map <String, Integer> count = new HashMap <String, Integer> ();

    int count_more_1 = 0;
    
    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);

      boolean bigger_than_1 = false;
      
      for (int i = 2; i < tokens.length; i++)
      {
        //System.out.println(tokens[i]);

        Matcher m = p_extract.matcher(tokens[i]);

        if (m.find())
        {
          count.put(m.group(1),
            (count.get(m.group(1)) == null ?  1 : count.get(m.group(1)) + 1)
        		  );

          if (Double.parseDouble(m.group(1)) > 5)
          { bigger_than_1 = true;}
        }
        else
        {
          System.out.println("Not matched: " + tokens[i]);
          System.out.println(line);
        }
      }
      
      if (bigger_than_1)
      { count_more_1++; }
    }

    b.close();
    
    for (Map.Entry <String, Integer> entry : count.entrySet())
    {
      System.out.println(entry.getKey() + "|" + entry.getValue());
    }

    System.out.println("**" + count_more_1);
  }
}