package readbiomed.mme.mesh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
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
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class MTIFeatures
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
  
  public static void main(String [] argc)
  throws IOException, ReSyntaxException, CompileDfaException
  {
	Pattern p = Pattern.compile("\\|");

	if (argc.length != 2)
    {
      System.err.println("MTIFeatures MH mti_file_gzipped");
      System.exit(-1);
    }

    String term = argc[0];
    String mti_file_name = argc[1];

    BufferedReader b = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(mti_file_name))));

    String line;

    while ((line = b.readLine()) != null)
    {
      String [] tokens = p.split(line);

      if (tokens[1].equals(term))
      {
        if (tokens[6].contains("MM"))
        { MM.add(tokens[0]); }

        if (tokens[6].contains("RC"))
        { RtM.add(tokens[0]); }
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