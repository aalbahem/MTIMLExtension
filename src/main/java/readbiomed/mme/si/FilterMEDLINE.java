package readbiomed.mme.si;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import monq.jfa.AbstractFaAction;
import monq.jfa.CallbackException;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;

public class FilterMEDLINE
{
  private static BufferedWriter w = null;

  private static Dfa dfa = null;
  private static Dfa dfaCitation = null;

  private static Set <String> journals = new HashSet <String> ();

  private static AbstractFaAction get_medline_citation = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{
      Map <String, String> map = Xml.splitElement(yytext, start);

      if (map.get("Status").equals("MEDLINE") || map.get("Status").equals("PubMed-not-MEDLINE"))
      {
    	try
    	{
   	      DfaRun dfaRun = new DfaRun(dfaCitation);
   	      dfaRun.clientData = new Boolean(false);
    	  dfaRun.filter(yytext.substring(start));

    	  if ((Boolean)dfaRun.clientData)
    	  {
    	    w.write(yytext.substring(start));
    	    w.newLine();
    	  }
    	}
    	catch (Exception e)
    	{ e.printStackTrace(); }
      }
  	}
  };

  private static AbstractFaAction get_journal_title = new AbstractFaAction()
  {
	public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
  	{
      Map <String, String> map = Xml.splitElement(yytext, start);

      if (journals.contains(map.get(Xml.CONTENT)))
      { runner.clientData = new Boolean(true); }
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
      //nfaCitation.or(Xml.GoofedElement("Title"), get_journal_title);
      nfaCitation.or(Xml.GoofedElement("MedlineTA"), get_journal_title);
      dfaCitation = nfaCitation.compile(DfaRun.UNMATCHED_COPY);
    }
    catch (Exception e)
    { e.printStackTrace(); }
  }

  public static void main (String [] argc)
  throws IOException
  {
	BufferedReader b = new BufferedReader(new FileReader(argc[0]));

	String line;

	while ((line = b.readLine()) != null)
	{
	  if (line.trim().length() > 0)
	  { journals.add(line.trim()); }
	}

    b.close();
    
    w = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("output.gz"))));
    
    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(System.in));
    dfaRun.filter();      

    w.close();
  }
}