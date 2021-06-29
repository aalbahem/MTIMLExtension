package readbiomed.mme.util;

import java.io.IOException;

import monq.jfa.CompileDfaException;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReSyntaxException;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;
import monq.jfa.actions.Printf;

/**
 * 
 * Remove title and abstract from the MEDLINE citation.
 * 
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 *
 */
public class RemoveTitleAbstract
{
  public static void main(String [] argc)
  throws IOException, ReSyntaxException, CompileDfaException
  {
    Nfa nfa = new Nfa(Nfa.NOTHING);
    nfa.or(Xml.GoofedElement("ArticleTitle"), new Printf(""));
    nfa.or(Xml.GoofedElement("AbstractText"), new Printf(""));
    Dfa dfa = nfa.compile(DfaRun.UNMATCHED_COPY);

    DfaRun dfaRun = new DfaRun(dfa);
    dfaRun.setIn(new ReaderCharSource(System.in, "UTF-8"));
    dfaRun.filter(System.out);
  }
}