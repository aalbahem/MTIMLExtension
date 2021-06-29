package readbiomed.mme.si;

import java.io.IOException;
import java.util.Map;

import monq.jfa.AbstractFaAction;
import monq.jfa.CallbackException;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;

public class SICategory
{
  private static Dfa dfa = null;

  private static boolean indexed = false;

  private static AbstractFaAction get_medline_citation_start = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{
      Map <String, String> map = Xml.splitElement(yytext, start);

      if (map.get("Status").equals("MEDLINE"))
      { indexed = true; }
      else
      { indexed = false; }
  	}
  };

  private static AbstractFaAction get_medline_citation_end = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{
      if (indexed)
      { yytext.insert(start, "<Category>Indexed</Category>"); }
      else
      { yytext.insert(start, "<Category>NotIndexed</Category>"); }
  	}
  };

  static
  {
	try
	{
      Nfa nfa = new Nfa(Nfa.NOTHING);
      nfa.or(Xml.STag("MedlineCitation"), get_medline_citation_start);
      nfa.or(Xml.ETag("MedlineCitation"), get_medline_citation_end);
      dfa = nfa.compile(DfaRun.UNMATCHED_COPY);
    }
    catch (Exception e)
    { e.printStackTrace(); }
  }

  public static void main (String [] argc) throws IOException
  {
    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(System.in));
    dfaRun.filter(System.out);      
  }
}