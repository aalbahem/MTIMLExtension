package readbiomed.mme.mesh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class AddCategory
{
  private static Set <String> pmids = new HashSet <String> ();

  private static String PMID = null;
  
  private static String category = null;

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
    	if (pmids.contains(PMID))
    	{
          yytext.insert(start,
        		        "<Category>" +
                        category +
                        "</Category>"
          );
    	}
      }
      
      PMID = null;
	}
  };
  
  public static void main(String [] argc)
  throws IOException, ReSyntaxException, CompileDfaException
  {
	if (argc.length != 2)
    {
      System.err.println("AddCategory category pmid_file_name");
      System.exit(-1);
    }

    category = argc[0];
    String pmid_file_name = argc[1];

    BufferedReader b = new BufferedReader(new FileReader(pmid_file_name));

    String line;

    while ((line = b.readLine()) != null)
    { pmids.add(line.trim()); }

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