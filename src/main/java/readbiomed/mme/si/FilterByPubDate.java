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

public class FilterByPubDate
{
  private static Dfa dfa = null;
  private static Dfa dfaCitation = null;
  private static boolean in_pub_date = false;
  private static boolean keep_citation = false;
	
  private static AbstractFaAction get_pub_date_start = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{ in_pub_date = true; }
  };
  
  private static AbstractFaAction get_pub_date_end = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{ in_pub_date = false; }
  };
  
  private static AbstractFaAction get_year = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{
      if (in_pub_date)
      {
        Map <String, String> map = Xml.splitElement(yytext, start);

        try
        {
          int year = Integer.parseInt(map.get(Xml.CONTENT));
          if (year >= 2000)
          { keep_citation = true; }
        }
        catch (Exception e)
        {}
      }
  	}
  };
  
  private static AbstractFaAction get_medline_citation = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{
      try
      {
        DfaRun dfaRun = new DfaRun(dfaCitation);
   	    dfaRun.clientData = new Boolean(false);
    	dfaRun.filter(yytext.substring(start));
    	  
    	if (!keep_citation)
    	{ yytext.delete(start, yytext.length()); }

      }
   	  catch (Exception e)
      { e.printStackTrace(); }
      
      keep_citation = false;
  	}
  };

  static
  {
	try
	{
      Nfa nfa = new Nfa(Nfa.NOTHING);
      nfa.or(Xml.GoofedElement("MedlineCitation"), get_medline_citation);
      dfa = nfa.compile(DfaRun.UNMATCHED_COPY);

      Nfa nfaCitation = new Nfa(Nfa.NOTHING);
      nfaCitation.or(Xml.STag("PubDate"), get_pub_date_start);
      nfaCitation.or(Xml.ETag("PubDate"), get_pub_date_end);
      nfaCitation.or(Xml.GoofedElement("Year"), get_year);
      dfaCitation = nfaCitation.compile(DfaRun.UNMATCHED_COPY);
    }
    catch (Exception e)
    { e.printStackTrace(); }
  }
  
  public static void main (String [] argc)
  throws IOException
  {
    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(System.in));
    dfaRun.filter(System.out);    
  }
}
