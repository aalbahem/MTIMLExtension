package readbiomed.mme.util.pipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class PipeMTI
{
  private static Pattern p = Pattern.compile("\\|");
	
  private static void generateFile(String annotations_file_name,
		                           String categories_file_name,
		                           String output_file_name
		                           )
  throws IOException
  {
	Map <String, Set <String>> pmid_categories =
			new HashMap <String, Set <String>> ();

	// Load categories
	{
      BufferedReader b =
    		  new BufferedReader(
    				  new InputStreamReader(
    						  new GZIPInputStream(
    								  new FileInputStream(categories_file_name))));

      String line;

      while ((line = b.readLine()) != null)
      {
    	String [] tokens = p.split(line);

    	Set <String> categories = pmid_categories.get(tokens[0]);
    	
    	if (categories == null)
    	{
          categories = new HashSet <String> ();
          pmid_categories.put(tokens[0], categories);
    	}

    	categories.add(tokens[1]);
      }

      b.close();
	}

	// Load MTI annotation
	Map <String, Set <String>> pmid_annotations =
			new HashMap <String, Set <String>> ();

	{
      BufferedReader b =
    		  new BufferedReader(
    				  new InputStreamReader(
    						  new GZIPInputStream(
    								  new FileInputStream(annotations_file_name))));

      String line;

      while ((line = b.readLine()) != null)
      {
    	String [] tokens = p.split(line);

    	Set <String> categories = pmid_annotations.get(tokens[0]);

    	if (categories == null)
    	{
          categories = new HashSet <String> ();
          pmid_annotations.put(tokens[0], categories);
    	}

    	try
    	{
    	categories.add(tokens[1]);
    	}
    	catch (Exception e)
    	{e.printStackTrace(); System.err.println(line);}
      }

      b.close();
	}

	// Generate the pipe file
    BufferedWriter w =
    		new BufferedWriter (
    				new OutputStreamWriter(
    						new GZIPOutputStream(
    								new FileOutputStream(output_file_name)
    								)
    			        )
                );

    for (Map.Entry <String, Set <String>> doc : pmid_categories.entrySet())
    {
      StringBuilder output_doc = new StringBuilder(doc.getKey()).append("|MTI");

      if (pmid_annotations.get(doc.getKey()) != null)
      {
        for (String term : pmid_annotations.get(doc.getKey()))
        { output_doc.append("|").append(term); }

        w.write(output_doc.toString());
        w.newLine();
      }

      StringBuilder output_cat = new StringBuilder(doc.getKey()).append("|MH");

      for (String cat : pmid_categories.get(doc.getKey()))
      { output_cat.append("|").append(cat); }
      
      w.write(output_cat.toString());
      w.newLine();
    }

    w.flush();
    w.close();
  }

  public static void main (String [] argc)
  throws IOException
  {
	  /*
	generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.train.gz",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\train.mti.gz"
    );

	generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.test.gz",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\test.mti.gz"
    );*/

	// MM data
	/*generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.train.MM.gz",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\train.mti.mm.gz"
    );

    generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.test.MM.gz",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\test.mti.mm.gz"
    );

	// RC data
	generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.train.RC.gz",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\train.mti.rc.gz"
    );

    generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.test.RC.gz",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\test.mti.rc.gz"
    );*/
	  
	// MM data
	generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.train.RtM.gz",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\train.mti.RtM.gz"
    );

    generateFile("C:\\Users\\ajimeno\\Dropbox\\NICTA_UNED_feature_engineering_2014\\data\\MTI\\MTI.test.RtM.gz",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\test.mti.RtM.gz"
    );
  }
}