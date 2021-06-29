package readbiomed.mme.util;

public class NGrams
{
  private String [] buffer = null;

  public NGrams (int size)
  { buffer = new String [size]; }

  public String [] add (String term)
  {
    String [] result = new String [buffer.length];

    // Organize the buffer
    for (int i = 0; i < buffer.length -1; i++)
    { buffer[i] = buffer[i+1]; }

    buffer[buffer.length-1] = term;

    StringBuilder string = new StringBuilder();

    for (int i = buffer.length - 1; i >= 0; i--)
    {
  	  if (string.length() == 0)
  	  { 
  	    result[i] = buffer[i];
  	    string.append(buffer[i]);
  	  }
  	  else
  	  {
  	    if (buffer[i] != null)
  	    {
          string.insert(0, " ");
          string.insert(0, buffer[i]);

          result[i] = string.toString();
  	    }
  	    else
  	    { break; }
  	  }
    }

    // Deliver the strings
    return result;
  }
}
