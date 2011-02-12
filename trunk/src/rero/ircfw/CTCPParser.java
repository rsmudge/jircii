/**
       .;: Parser for CTCP Protocol :;.

       Parses a hashmap for the following possibilities:
       - an ACTION
       - a CTCP Request
       - a CTCP Reply
       - (DCC stuff is a possibility later)
      
**/

package rero.ircfw;

import rero.ircfw.interfaces.FrameworkConstants;

import java.util.regex.Pattern;
import java.util.HashMap;
import rero.util.StringParser;

public class CTCPParser implements FrameworkConstants
{
   protected static String ctcpPattern = "\001(\\w++)(\\s*)(.*)\001";

   protected static Pattern isCTCP = Pattern.compile(ctcpPattern);

   public HashMap parseEvent (HashMap eventData)
   {
       String event, parms, data, target;

       String type, parameters;

       event  = (String)eventData.get($EVENT$);
       parms  = (String)eventData.get($PARMS$);
       target = (String)eventData.get($TARGET$);
 
       if ((! event.equals("NOTICE") && ! event.equals("PRIVMSG")) || parms == null )
       {
           return eventData;
       }

       StringParser parser = new StringParser(parms, isCTCP);

       if (!parser.matches())
       {                       // definetly not a CTCP we want nothing to do with it then.
           return eventData;
       }
       
       type       = parser.getParsedString(0); // thank god for regex's this would have taken forever
       parameters = parser.getParsedString(2); // to code out before.

       parms = type + " " + parameters;
       data  = target + " " + parms;       

       if (event.equals("PRIVMSG") && type.equals("ACTION"))
       {
          event = "ACTION";
          data  = target + " " + parameters;       

          String whitespace = parser.getParsedString(1);
          if (whitespace.length() > 1)
          {
             parameters = whitespace.substring(1) + parameters;
          }

          parms = parameters;
       }

       if (event.equals("PRIVMSG"))
       {
          event = "REQUEST";
       }

       if (event.equals("NOTICE"))
       {
          event = "REPLY";
       }

       eventData.put($DATA$,  data);
       eventData.put($PARMS$, parms); 
       eventData.put($EVENT$, event);
       eventData.put($TYPE$,  type);

       return eventData;
   }

}
