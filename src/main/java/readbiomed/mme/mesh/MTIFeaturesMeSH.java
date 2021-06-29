package readbiomed.mme.mesh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import monq.jfa.AbstractFaAction;
import monq.jfa.CallbackException;
import monq.jfa.CompileDfaException;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReSyntaxException;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;

/**
 * 
 * Generate features from MTI. Features are placed under the AdditionalFeatures tag.
 * Considers providing the terms based on MeSH
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class MTIFeaturesMeSH
{
  private static Set <String> RtM = new HashSet <String> ();
  private static Set <String> MM = new HashSet <String> ();

  private static String PMID = null;

  private static AbstractFaAction get_pmid = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{
      Map <String, String> map = Xml.splitElement(yytext, start);
      if (PMID == null) { PMID = map.get(Xml.CONTENT); }
	}
  };

  private static AbstractFaAction get_medline_citation = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{
      if (PMID != null)
      {
    	StringBuilder features = new StringBuilder();

        if (RtM.contains(PMID))
        { features.append("RC"); }

        if (MM.contains(PMID))
        { features.append(" MM"); }

        if (features.length() > 0)
        {
          yytext.insert(start,
        		        "<AdditionalFeatures>" +
                        features.toString().trim() +
                        "</AdditionalFeatures>"
          );
        }
      }

      PMID = null;
	}
  };

  private static Map <String, List <String>> readMeSH(String mesh_file_name)
  throws IOException
  {
    Map <String, List <String>> term_codes =
	    		new HashMap <String, List <String>> (); 

	Pattern p_sc = Pattern.compile(";");

    BufferedReader b = new BufferedReader(new FileReader(mesh_file_name));

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

	return term_codes;
  }

  /** 
   * Check if the mapped term is below in the hierarchy or if is the same term
   * 
   * @param term
   * @param mapped
   * @return
   * @throws IOException
   */
  private static boolean checkTermMeSH(String term, String mapped, Map <String, List <String>> mesh)
  throws IOException
  {
    List <String> cr = mesh.get(term);

    List <String> tc = mesh.get(mapped);

    if (cr != null && tc != null)
    {
   	  for (String t : tc)
	  {
	    for (String c : cr)
	    {
          if (t.startsWith(c) || t.equals(c))
	      { return true; }
	    } 
	  }
	}

	return false;
  }
  
  public static void main(String [] argc)
  throws IOException, ReSyntaxException, CompileDfaException
  {
	Pattern p = Pattern.compile("\\|");

	if (argc.length != 3)
    {
      System.err.println("MTIFeaturesMeSH MH mti_file_gzipped mesh_file");
      System.exit(-1);
    }

    String [] terms = argc[0].split(";");
    String mti_file_name = argc[1];
    
    Map <String, List <String>> mesh = readMeSH(argc[2]);

    BufferedReader b = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(mti_file_name))));

    String line;

    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);

      for (String term : terms)
      {
        if (checkTermMeSH(term, tokens[1], mesh))
        {
          if (tokens[6].contains("MM"))
          { MM.add(tokens[0]); }

          if (tokens[6].contains("RC"))
          { RtM.add(tokens[0]); }
        }
      }
    }

    b.close();

    Nfa nfa = new Nfa(Nfa.NOTHING);
    nfa.or(Xml.GoofedElement("PMID"), get_pmid);
    nfa.or(Xml.ETag("MedlineCitation"), get_medline_citation);
    Dfa dfa = nfa.compile(DfaRun.UNMATCHED_COPY);

    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(System.in, "UTF-8"));
    dfaRun.filter(System.out);
  }
}