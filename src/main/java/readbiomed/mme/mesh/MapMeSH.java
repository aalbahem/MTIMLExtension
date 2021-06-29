package readbiomed.mme.mesh;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class MapMeSH
{
  public static void main (String [] argc) throws IOException
  {
	
    Map <String, List <String>> term_codes =
    		new HashMap <String, List <String>> (); 

    {
	Pattern p_sc = Pattern.compile(";");

    BufferedReader b = new BufferedReader(new FileReader(argc[3]));

    String line;


    while ((line = b.readLine()) != null)
    {
      String [] tokens = p_sc.split(line);

      List <String> codes = term_codes.get(tokens[0]);
 
      if (codes == null)
      {
    	codes = new ArrayList <String> ();
    	term_codes.put(tokens[0], codes);
      }
      
      codes.add(tokens[1]);
    }

    b.close();
    }
    
    Pattern p = Pattern.compile("\\|");
    
    String mh = argc[0];
    
    List <String> cr = term_codes.get(mh);

    if (cr == null)
    { System.err.println("Error mapping term"); }
    else
    { System.out.println(cr); }

    BufferedReader b = new BufferedReader(new FileReader(argc[1]));
    
    BufferedWriter w = new BufferedWriter(new FileWriter(argc[2]));
    
    String line;
    
    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);
      
      List <String> tc = term_codes.get(tokens[1]);
      
      if (tc == null)
      { //System.err.println("Not found: " + tokens[1]);
      }
      else
      {

    	for (String t : tc)
    	{
    	  for (String c : cr)
    	  {
    		if (t.startsWith(c) || t.equals(c))
    		{
    		  w.write(tokens[0] + "|" + mh +"|" + tokens[1]);
    		  w.newLine();
    		}
    	  } 
    	}
    	  
      }
    }
    
    b.close();
    w.close();
  }
}