package readbiomed.mme.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class FilterPipePMID
{
  public static void main (String [] argc)
  throws IOException
  {
	Pattern p = Pattern.compile("\\|");
	
	Set <String> pmids = new HashSet <String> ();
	
	{
	  BufferedReader b = new BufferedReader(new FileReader(argc[0]));

	  String line;
	  
	  while ((line = b.readLine()) != null)
	  { pmids.add(line.trim()); }
	  
	  b.close();
	}

	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
	
	String line;
	
	while ((line = b.readLine()) != null)
	{
	  String [] tokens = p.split(line);
	  
	  if (pmids.contains(tokens[0]))
	  { System.out.println (line); }
	}
	
    b.close();	
  }
}
