package readbiomed.mme.util;

import java.io.IOException;

import monq.jfa.AbstractFaAction;
import monq.jfa.CallbackException;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;

public class AdditionalFeatures
{
  private static Dfa dfa = null;

  private static String title = null;
  private static String vernacular_title = null;
  private static String language = null;
  private static StringBuilder abstract_text = new StringBuilder();

  private static AbstractFaAction get_article_title = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{ title = Xml.splitElement(yytext, start).get(Xml.CONTENT); }
  };
  
  private static AbstractFaAction get_abstract_text = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{ abstract_text.append(" ").append(Xml.splitElement(yytext, start).get(Xml.CONTENT)); }
  };
  
  private static AbstractFaAction get_vernacular_title = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{ vernacular_title = Xml.splitElement(yytext, start).get(Xml.CONTENT); }
  };
  
  private static AbstractFaAction get_language = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{ language = Xml.splitElement(yytext, start).get(Xml.CONTENT); }
  };

  private static AbstractFaAction get_medline_citation_end = new AbstractFaAction()
  {
    public void invoke(StringBuffer yytext,int start,DfaRun runner)
  	throws CallbackException
	{ 
      StringBuilder additional_features = new StringBuilder("<AdditionalFeatures>"); 

	  if (title != null && title.startsWith("["))
	  { additional_features.append(" hastitlebrackets"); }

	  if (abstract_text.toString().trim().length() != 0)
	  { additional_features.append(" hasabstract"); }

	  if (vernacular_title != null)
	  { additional_features.append(" hasvernaculartitle"); }

	  if (language != null)
	  { additional_features.append(" languageXXX").append(language); }

	  additional_features.append("</AdditionalFeatures>");

	  yytext.insert(start, additional_features);

	  title = null;
	  abstract_text.setLength(0);
	  vernacular_title = null;
	  language = null;
	}
  };

  static
  {
	try
	{
      Nfa nfa = new Nfa(Nfa.NOTHING);
      nfa.or(Xml.GoofedElement("AbstractText"), get_abstract_text);
      nfa.or(Xml.GoofedElement("ArticleTitle"), get_article_title);
      nfa.or(Xml.GoofedElement("VernacularTitle"), get_vernacular_title);
      nfa.or(Xml.GoofedElement("Language"), get_language);
      nfa.or(Xml.ETag("MedlineCitation"), get_medline_citation_end);
      dfa = nfa.compile(DfaRun.UNMATCHED_COPY);
	}
	catch (Exception e)
	{
      e.printStackTrace();
      System.exit(-1);
	}
  }

  public static void main (String [] argc)
  throws IOException
  {
    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(System.in, "UTF-8"));
    dfaRun.filter(System.out);
  }
}