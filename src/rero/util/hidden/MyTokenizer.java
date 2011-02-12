package rero.util.hidden;

import java.util.*;

public class MyTokenizer
{
     String string;
     String token;
     int end = 0, start = 0;
     int count;

     public String nextToken()
     { 
        if ((end + token.length()) >= string.length())
        {
           return string.substring(start, string.length());
        }

        while (end < string.length() && !string.substring(end, end + token.length()).equals(token))
        {
           if ((end + token.length()) >= string.length())
           {
              return string.substring(start, string.length());
           }
           end++;           
        }

        int multiple = 0;
        for (int x = end; string.substring(x, x + token.length()).equals(token); x++)
        {
           multiple++;
           if ((end + (token.length() * multiple)) >= string.length())
           {
              return string.substring(start, string.length() - (token.length() * multiple));
           }
        }

        String value = string.substring(start, end);
        start = end + (token.length() * multiple);
        end = end + (token.length() * multiple);

        return value;
     }

     public MyTokenizer (String s, String t)
     {
        string = new String(s);
        token = t;

        while ((start+token.length()) < string.length() && string.substring(start, start + token.length()).equals(token))
        {
           start++;
           end++;
        }

        int c = start;
        count = 1;
        while ((c + token.length()) < string.length())
        {
           if ((string.substring(c, c + token.length()).equals(token)))
           {
              count++;
              while ((c + token.length()) < string.length() && (string.substring(c, c + token.length()).equals(token)))
              {
                c++;
              }
           }
           c++;
        }
        if (s.length() == 0)
        {
           count = 0;
        }
     }

     public int countTokens ()
     {
        return count;        
     }
}
