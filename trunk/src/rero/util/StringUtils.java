package rero.util;

import java.util.*;
import text.*;

public class StringUtils
{
     /** strips the color codes out of the specified text */
     public static String strip(String text)
     {
        return AttributedString.CreateAttributedString(text).getText();
     }
   
     //
     // iswm = is wildcard match = wildcard matching, pHEER iT!
     //
     public static boolean iswm(String a, String b)
     {
        int aptr = 0, bptr = 0, cptr;

        while (aptr < a.length())
        {
           if (a.charAt(aptr) == '*')
           {
              while (a.charAt(aptr) == '*')
              {
                 aptr++;
                 if (aptr == a.length())
                 {
                    return true;
                 }
              }

              for (cptr = aptr; cptr < a.length() && a.charAt(cptr) != '?' && a.charAt(cptr) != '*'; cptr++);

 //             bptr = b.indexOf(a.charAt(aptr), bptr);
              bptr = b.indexOf(a.substring(aptr, cptr), bptr);
              if (bptr == -1)
              {
//                 System.out.println("FP: No index of "+a.charAt(aptr)+" from "+bptr);
                 return false;
              }
           }
           else if (a.charAt(aptr) == '?')
           {
              while (a.charAt(aptr) == '?')
              {
                 bptr++;
                 aptr++;
                 if (aptr == a.length())
                 {
  //                  System.out.println("Possible FP: "+bptr+" == "+b.length());
                    return (bptr == b.length());
                 }
              }
           }
           else if (a.charAt(aptr) != b.charAt(bptr))
           {
    //          System.out.println("FP: "+a.charAt(aptr)+" != "+b.charAt(bptr)+" Chars ("+aptr+", "+bptr+")");
              return false;
           }
           aptr++;
           bptr++;
        }
        return true;
     }
}
