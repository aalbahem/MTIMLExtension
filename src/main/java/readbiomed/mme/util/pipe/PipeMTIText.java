package readbiomed.mme.util.pipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PipeMTIText
{
  public static void main (String [] argc) throws IOException
  {
	Pattern p = Pattern.compile("\\|");

	// Read the MTI annotations (argc[0]) for the specified MeSH heading (argc[1])
	Set <String> pmids = new HashSet <String> ();

	Set <String> pmids_processed = new HashSet <String> ();

	{
	  String file_name = argc[0];
	  String heading = argc[1];

	  {
	    BufferedReader b =
			new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("C:\\datasets\\amia_set\\pipe\\MTI.train.gz"
											)
									)
							)
					);

	    String line;

	    while ((line = b.readLine()) != null)
	    {
          String [] tokens = p.split(line);

          if (tokens[1].equals(heading))
          { pmids.add(tokens[0]); }
	    }

	    b.close();
	  }

	  {
	    BufferedReader b =
			new BufferedReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream("C:\\datasets\\amia_set\\pipe\\MTI.test.gz"
											)
									)
							)
					);

	    String line;

	    while ((line = b.readLine()) != null)
	    { 
          String [] tokens = p.split(line);

          if (tokens[1].equals(heading))
          { pmids.add(tokens[0]); }
	    }

	    b.close();
	  }
	}

	// Read the annotation file and add the MTI feature
	{
	  BufferedWriter w =
				new BufferedWriter(
						new OutputStreamWriter(
								new GZIPOutputStream(
										new FileOutputStream("C:\\datasets\\amia_set\\pipe\\train.unigram.mti.gz"
												)
										)
								)
						);
		
	  BufferedReader b =
				new BufferedReader(
						new InputStreamReader(
								new GZIPInputStream(
										new FileInputStream("C:\\datasets\\amia_set\\pipe\\train.unigram.gz"
												)
										)
								)
						);

	  String line;

	  while ((line = b.readLine()) != null)
	  {
		String [] tokens = p.split(line);
		
		if (!pmids_processed.contains(tokens[0]))
		{
		  pmids_processed.add(tokens[0]);

          if (pmids.contains(tokens[0]))
          {
        	w.write(tokens[0] + "|MTI|1");
            w.newLine();
          }
		}
		
		w.write(line);
		w.newLine();
	  }

	  w.flush();
	  w.close();
	  b.close();
	}
	
	{
	  BufferedWriter w =
					new BufferedWriter(
							new OutputStreamWriter(
									new GZIPOutputStream(
											new FileOutputStream("C:\\datasets\\amia_set\\pipe\\test.unigram.mti.gz"
													)
											)
									)
							);

	  BufferedReader b =
					new BufferedReader(
							new InputStreamReader(
									new GZIPInputStream(
											new FileInputStream("C:\\datasets\\amia_set\\pipe\\test.unigram.gz"
													)
											)
									)
							);

	  String line;

	  while ((line = b.readLine()) != null)
	  {
		String [] tokens = p.split(line);

		if (!pmids_processed.contains(tokens[0]))
		{
		  pmids_processed.add(tokens[0]);

          if (pmids.contains(tokens[0]))
          {
        	w.write(tokens[0] + "|MTI|1");
            w.newLine();
          }
		}

		w.write(line);
		w.newLine();
	  }

	  w.flush();
	  w.close();
	  b.close();
	}
  }
}