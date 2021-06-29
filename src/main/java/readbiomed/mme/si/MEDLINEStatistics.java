package readbiomed.mme.si;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import monq.jfa.AbstractFaAction;
import monq.jfa.CallbackException;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;

public class MEDLINEStatistics
{
  private static Dfa dfa = null;

  private static Map <String, Integer> journal_indexed =
		  new TreeMap <String, Integer> ();

  private static Map <String, Integer> journal_not_indexed =
		  new TreeMap <String, Integer> ();

  private static boolean indexed = false;
  private static String journal = null;

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
	  if (journal != null)
	  {
		if (indexed)
		{
		  Integer count = journal_indexed.get(journal);

		  if (count == null)
		  { journal_indexed.put(journal, 1); }
		  else
		  { journal_indexed.put(journal, count + 1); }
		}
		else
		{
		  Integer count = journal_not_indexed.get(journal);

		  if (count == null)
		  { journal_not_indexed.put(journal, 1); }
		  else
		  { journal_not_indexed.put(journal, count + 1); }
		}
	  }

	  indexed = false;
	  journal = null;
  	}
  };

  private static AbstractFaAction get_journal_title = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{ journal = Xml.splitElement(yytext, start).get(Xml.CONTENT); }
  };

  static
  {
	try
	{
      Nfa nfa = new Nfa(Nfa.NOTHING);
      nfa.or(Xml.STag("MedlineCitation"), get_medline_citation_start);
      nfa.or(Xml.ETag("MedlineCitation"), get_medline_citation_end);
      nfa.or(Xml.GoofedElement("MedlineTA"), get_journal_title);
      dfa = nfa.compile(DfaRun.UNMATCHED_COPY);
    }
    catch (Exception e)
    { e.printStackTrace(); }
  }

  public static void main (String [] argc) throws IOException
  {
    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(new GZIPInputStream(new FileInputStream(argc[0]))));
    dfaRun.filter();      

    Set <String> journals = new TreeSet <String> ();
    
    journals.addAll(journal_indexed.keySet());
    journals.addAll(journal_not_indexed.keySet());
    
    System.out.println("Journals: " + journals.size());
    
    for (String journal : journals)
    {
      System.out.println(journal + "|" + journal_indexed.get(journal) + "|" + journal_not_indexed.get(journal));
    }
  }
}