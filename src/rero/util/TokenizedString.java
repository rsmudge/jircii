package rero.util;

import java.util.*;
import rero.util.hidden.MyTokenizer;

public class TokenizedString
{
    protected String text;

    protected String tokenDelimeter;
    protected String stringToken[];
    protected String tokenUpCache[];
    protected String tokenDownCache[];

    protected int totalTokens;

    public String getString()
    {
       return text;
    }

    public TokenizedString(String str) 
    {
       text = str;
    }

    public TokenizedString(String str, String delim)
    {
       text = str;
       tokenize(delim);
    }

    public int tokenize(String delim) 
    {                                      /* return number of tokens */
       MyTokenizer erect = new MyTokenizer(text, delim);
       stringToken       = new String[erect.countTokens()];
       tokenUpCache      = new String[erect.countTokens()]; 
       tokenDownCache    = new String[erect.countTokens()]; 

       totalTokens       = stringToken.length;
       tokenDelimeter    = delim;

       for (int x = 0; x < totalTokens; x++)
       {
          stringToken[x] = erect.nextToken();
       }

       return totalTokens;
    }

    /** returns tokens from and including a up to and including b */
    public String getTokenRange(int a, int b) 
    {
        if (a == 0)  // special case.
        {
           return getTokenTo(b);
        }

        if (a == b) 
        { 
           return ""; 
        }

        if (a > b)
        { 
           return "";
        }

        String startOfRange = getTokenTo(a);
        String endOfRange   = getTokenTo(b + 1); // we want the token range to include both the beginning and
                                                 // end token in the range.   This is why we have the +1 hack.

        int lenStart, lenEnd, lenDelim, lenText;
        lenStart =   startOfRange.length();
        lenEnd   =     endOfRange.length();        
        lenDelim = tokenDelimeter.length();
        lenText  =           text.length();

        //
        // strip out the pesky deliminator...
        // 
        while ((lenStart + lenDelim) < lenText && text.substring(lenStart, lenStart + lenDelim).equals(tokenDelimeter))
        {
           lenStart += lenDelim;
        }

        return text.substring(lenStart, lenEnd); 
    }

    /** returns tokens up to and including x (starting at a 0 count) */
    public String getTokenTo(int x) 
    { 
        if (x >= tokenDownCache.length)  // special case.
        {
           return text;
        }

        /* get token x on down (tokens preserved) */ 
        if (tokenDownCache[x] == null) 
        {
           tokenDownCache[x] = gettokdn(text, x, tokenDelimeter);
        } 
        return tokenDownCache[x];
    }

    /** returns tokens from and including x (starting at a 0 count) */
    public String getTokenFrom(int x) 
    { 
        if (x >= tokenDownCache.length)  // special case.
        {
           return "";
        }

        /* get token x on up (tokens preserved) */
        if (tokenUpCache[x] == null) 
        {
           tokenUpCache[x] = gettokup(text, x, tokenDelimeter);
        }
        return tokenUpCache[x];
    }

    public boolean isTokenized(String text, String delim) 
    {
       if ((text.equals(this.text)) && (delim.equals(tokenDelimeter)))
       { 
          return true; 
       }
       return false;
    }

    public int getTotalTokens() 
    { 
       return totalTokens; 
    }  

    /** returns token x */
    public String getToken(int x) 
    { 
       /* return token x */
       if (x < totalTokens) 
       {
          return stringToken[x];
       }
       return null;
    }

    public String toString() 
    {
       String t;
       t =   "---     jString:Report      ---\n";
       t = t+"Original String: "+text+"\n";
       t = t+"Token Delimeter: \""+tokenDelimeter+"\"\n";
       t = t+"Total Tokens   : "+totalTokens+"\n";
       t = t+"---       Token:Breakdown   ---\n";
       for (int x = 0; x < totalTokens; x++) 
       {
          t = t+x+")  "+getToken(x)+"\n";
       }
       t = t+"---         END:REPORT      ---\n";
       return t;
    }

    protected static String gettokup(String text, int occurence, String delim) 
    {
         if (occurence == 0)   // special case.. if the token we want up to is 0 then this is the whole string
         {
            return text;
         }

         String tokensPrior = gettokdn(text, occurence, delim);
         
         int lenPrior, lenDelim, lenText;

         lenPrior = tokensPrior.length();
         lenDelim =       delim.length();
         lenText  =        text.length();

         if (lenPrior < lenText) 
         {
            //
            // strip out the pesky deliminator...
            // 
            while ((lenPrior + lenDelim) < lenText && text.substring(lenPrior, lenPrior + lenDelim).equals(delim))
            {
               lenPrior += lenDelim;
            }

            return text.substring(lenPrior, lenText);   
         }

         return "";
     }

     protected static String gettokdn(String text, int occurence, String delim) 
     {
        if (occurence == 0)  // deal with the special case, from token 0 on down is an empty string.
        {
           return "";
        }

        int     occ =     0;     // occurence;

        int     dlen = delim.length();
        int     tlen =  text.length();

        for (int x = 0; x < tlen; x++)
        {
           if ((x + dlen) < tlen && text.substring(x, x + dlen).equals(delim))
           {
              occ++;

              if (occ == occurence)
              {
                  return text.substring(0, x);
              }

              while ((x + dlen) < tlen && text.substring(x, x + dlen).equals(delim))
              { 
                 x += dlen;
              }
           }
        }

        return text;
     }

     public boolean isToken(String token)
     {
        if (findToken(token) >= 0) 
        {
           return true;
        }
        return false;
     }

     public int findToken(String token)
     {
        for (int x = 0; x < getTotalTokens(); x++)
        {
           if (getToken(x).toUpperCase().equals(token.toUpperCase()))
           {
              return x;
           }
        }
        return -1;
     }
}
