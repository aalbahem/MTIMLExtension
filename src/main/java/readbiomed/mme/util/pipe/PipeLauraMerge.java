package readbiomed.mme.util.pipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

public class PipeLauraMerge
{
  private static Pattern p = Pattern.compile("\\|");
	
  public static void generateFile(String annotations_file_name,
                                  String categories_file_name,
                                  String output_file_name)
  throws FileNotFoundException, IOException
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

	String PMID = null;

	// Generate the pipe file
    BufferedWriter w =
    		new BufferedWriter (
    				new OutputStreamWriter(
    						new GZIPOutputStream(
    								new FileOutputStream(output_file_name)
    								)
    			        )
                );

    BufferedReader b =
    		new BufferedReader(
    				new FileReader(annotations_file_name));

    String line;

    Set <String> pmids = new HashSet <String> ();

    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);

      if (PMID != null)
      {
    	if (!PMID.equals(tokens[0]))
    	{
    	  StringBuilder output_cat = new StringBuilder(PMID).append("|MH");

    	  for (String cat : pmid_categories.get(PMID))
    	  { output_cat.append("|").append(cat); }

    	  w.write(output_cat.toString());
    	  w.newLine();
    	}
      }

      PMID = tokens[0];

      pmids.add(PMID);

      w.write(line);
      w.newLine();
    }

    if (PMID != null)
    {
  	  StringBuilder output_cat = new StringBuilder(PMID).append("|MH");

  	  for (String cat : pmid_categories.get(PMID))
  	  { output_cat.append("|").append(cat); }

  	  w.write(output_cat.toString());
  	  w.newLine();
    }

    Set <String> inter = new HashSet (pmid_categories.keySet());
    inter.removeAll(pmids);

    // Add the missing ones
    for (String pmid : inter)
    {
      StringBuilder output_cat = new StringBuilder(pmid).append("|MH");

	  for (String cat : pmid_categories.get(pmid))
	  { output_cat.append("|").append(cat); }

	  w.write(output_cat.toString());
	  w.newLine();
    }

    b.close();
    w.close();
  }

  public static void main (String [] argc) throws FileNotFoundException, IOException
  {
    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_1_CUIs\\Training_cuis_all.txt",
    		     "C:\\datasets\\amia_set\\train.mesh.gz",
    		     "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_1_CUIs\\train.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_1_CUIs\\Test_cuis_all.txt",
		         "C:\\datasets\\amia_set\\test.mesh.gz",
		         "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_1_CUIs\\test.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_2_concepts\\Training_concepts_all.txt",
		         "C:\\datasets\\amia_set\\train.mesh.gz",
		         "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_2_concepts\\train.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_2_concepts\\Test_concepts_all.txt",
	             "C:\\datasets\\amia_set\\test.mesh.gz",
	             "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_2_concepts\\test.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_phrases\\Training_phrases_all.txt",
	             "C:\\datasets\\amia_set\\train.mesh.gz",
	             "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_phrases\\train.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_phrases\\Test_phrases_all.txt",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
               	 "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_phrases\\test.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_primer_ancestro\\Training_ancestor_1_all.txt",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_primer_ancestro\\train.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_primer_ancestro\\Test_ancestor_1_all.txt",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
          	     "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_3_primer_ancestro\\test.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_4_segundo_ancestro\\Training_ancestor_2_all.txt",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_4_segundo_ancestro\\train.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_4_segundo_ancestro\\Test_ancestor_2_all.txt",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
     	         "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_4_segundo_ancestro\\test.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_5_tercer_ancestro\\Training_ancestor_3_all.txt",
                 "C:\\datasets\\amia_set\\train.mesh.gz",
                 "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_5_tercer_ancestro\\train.label.join.gz");

    generateFile("C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_5_tercer_ancestro\\Test_ancestor_3_all.txt",
                 "C:\\datasets\\amia_set\\test.mesh.gz",
	             "C:\\datasets\\amia_set\\pipe\\Ficheros_Laura_1\\Experimento_5_tercer_ancestro\\test.label.join.gz");
  }
}