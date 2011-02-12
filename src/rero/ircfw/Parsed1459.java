/**
       .;: Parser for RFC 1459 - IRC Protocol :;.

   Some of this code gets kind of ugly.  Regex's all over the place.  

   Basically this class takes a String given by the IRC server and extracts
   all relevant information we might care about into a Hashmap.

   No extra processing is done here, just extract information from the protocol

    Ugly code below.  You shouldn't need to touch this though.  That is why
    it is framework code.  !@#$%^

 BNF Representation of Stuff from Server

 <message> ::= ':' <prefix> <SPACE> <command> <params> 
               <command> <params>

 <prefix>  ::= <servername>
               <nick> [ '!' <user> ] [ '@' <host> ] 

 <command> ::= <letter> { <letter> }
               <number> <number> <number>

 <SPACE>   ::= ' ' { ' ' }

 <params>  ::= ':' <text>
           ::= <middle> <params>

  <middle> ::= a bunch of characters without the first one being a :, and no
               white space

<trailing> ::= a bunch of white space

**/

package rero.ircfw;

import rero.ircfw.interfaces.FrameworkConstants;

import java.util.regex.Pattern;
import java.util.HashMap;
import rero.util.StringParser;

public class Parsed1459 implements FrameworkConstants
{
   //
   // Yes, we are in write-only land with reg ex's.  Sorry.
   //  Nick Pattern: ([\-|\[|\]|\\|\`|\^|\{|\}|\w]++)
   //  Nick Pattern: ([^!\.]+?) <-- safer
   //  User Pattern: ([\~\w]++)
   //  User Pattern: ([^\@]+?)  <-- safer
   //  Host Pattern: (\w++[[\.|\:]\w++]*)  hopefully works with either ipv4 or ipv6 addresses. - if my IP6 bit is bad then this 
   //                                      will fuck up parsing royally and the client *WILL* crash.
   //  Chan Pattern: ([\#|\&]\S++)
   //
   protected static String nickPattern    = "(.+?)";
   protected static String userPattern    = "(.+?)";
   protected static String hostPattern2   = "(.+?)";

   protected static String hostPattern    = "([\\w\\-\\=]++[[\\.|\\:][\\w\\-\\=]++]*)";
   protected static String channelPattern = "([\\#|\\&|\\!|\\+]\\S++)";

   // ([\-|\[|\]|\\|\`|\^|\{|\}|\w|\-]++)!(([\~\w\-]++)@([\w\-]++[\.[\w\-]++]*))

   protected static Pattern isHost         = Pattern.compile(hostPattern);
   protected static Pattern isNickUserHost = Pattern.compile(nickPattern+"!("+userPattern+"@"+hostPattern2+")"); // matches nick!user@host 
   protected static Pattern isNick         = Pattern.compile(nickPattern);

   protected static Pattern isNumeric      = Pattern.compile("\\d++");
   protected static Pattern isColonPresent = Pattern.compile(":(.++)");
   protected static Pattern isWhiteSpace   = Pattern.compile("XXX");

   protected HashMap eventInformation;

   public HashMap parseString (String data)
   {
      eventInformation = new HashMap();
      
      eventInformation.put($RAW$, data);

      phase1(data, eventInformation);

      return eventInformation;
   }

   //
   // Phase 1 - grabs really basic info (source, command, parms)
   // -------
   //
   private HashMap phase1 (String data, HashMap eventInformation)
   {
       StringParser parser;

       parser = new StringParser(data, isColonPresent);
       if (parser.matches())
       {
           // <message> ::= ':' <prefix> <SPACE> <command> <params> 

           String[] text = parser.getParsedString(0).split("\\s", 2);

           parseSourceInformation(text[0], eventInformation);

           data = text[1];
       }

       // <message> ::= <command> <params>
       String text[] = data.split("\\s", 2);

       if (isNumeric.matcher(text[0]).matches())
       {
           eventInformation.put($NUMERIC$, text[0]);
       }

       eventInformation.put($EVENT$, text[0]);

       if (text.length > 1)
       {
          data = text[1];
       }
       else
       {
          data = "";
       }

       parseParameterInformation(data, eventInformation);

       return eventInformation;
   }

   private HashMap parseParameterInformation (String data, HashMap eventInformation)
   {
       // <params> ::= <SPACE> [ ':' <trailing> | <middle> <params> ]

       //
       // Generally the item after the COMMAND is a target or a set of parameters
       // right here we are looking to see if it is the target or not.
       //
       StringParser parser = new StringParser(data, isColonPresent);
       String text[] = data.split("\\s", 2);
       int targetLength = 0;       

       if (!parser.matches())
       {
           text = data.split("\\s", 2);
           eventInformation.put($TARGET$, text[0]);
           targetLength = text[0].length() + 1;
       }
       else
       {
           if ( "#&+!".indexOf(data.charAt(1)) > -1 && data.indexOf(' ') == -1 )
           {
               // the parameter is a channel, this is put in place for broke assed ircds
               // that don't know any better... grr.
               data = data.substring(1, data.length());

               return parseParameterInformation(data, eventInformation);
           }
           else
           {           
               data = "<null> " + data;
               targetLength = "<null>".length() + 1;
           }

           text = data.split("\\s", 2);
           parser = new StringParser(data, isColonPresent);
       }

       StringBuffer parmsString = new StringBuffer();
       
       while (!parser.matches() && text.length > 1)
       {
           text = data.split("\\s", 2);

           if (text.length > 1)
           {
              data = text[1];

              parmsString.append(text[0]);
              parmsString.append(" ");

              parser = new StringParser(data, isColonPresent);
           }
       }

       if (parser.matches())
       {
           parmsString.append(parser.getParsedString(0));
       }
       else if (!data.equals(":"))
       {
           parmsString.append(data);
       }

       eventInformation.put($DATA$, parmsString.toString());

       if (targetLength < parmsString.toString().length())
       {
          eventInformation.put($PARMS$, parmsString.toString().substring(targetLength, parmsString.toString().length()  ));
       }
       else
       {
          /* fix a bug with NICK event if necessary */
          if ("NICK".equals((String)eventInformation.get($EVENT$)))
          {
             eventInformation.put($DATA$, "<null> " + parmsString.toString());
             eventInformation.put($PARMS$, parmsString.toString());
          }
          else
          {
             eventInformation.put($PARMS$, "");
          }
       }  

       return eventInformation;
   }

   private HashMap parseSourceInformation(String text, HashMap eventInformation)
   {
      StringParser parser = new StringParser(text, isNickUserHost);
      if (parser.matches())
      {
          // Pattern> ([\-|\[|\]|\\|\`|\^|\{|\}|\w|\-]++)!(([\~\w\-]++)@([\w\-]++[\.[\w\-]++]*))
          // Text> `butane!~uncle@istheman.chartermi.net
          // Matches!
          // 0: `butane
          // 1: ~uncle@istheman.chartermi.net
          // 2: ~uncle
          // 3: istheman.chartermi.net

          String[] data = parser.getParsedStrings();
          eventInformation.put($NICK$,    data[0]);
          eventInformation.put($ADDRESS$, data[1]);
          eventInformation.put($USER$,    data[2]);
          eventInformation.put($HOST$,    data[3]);

          eventInformation.put($SOURCE$,  data[0]);

          return eventInformation;
      }
  
      parser = new StringParser(text, isHost);
      if (parser.matches())
      {
          String[] data = parser.getParsedStrings();
          eventInformation.put($SERVER$,  data[0]);
          eventInformation.put($NICK$,    data[0]);
          eventInformation.put($SOURCE$,  data[0]);

          return eventInformation;
      }

      parser = new StringParser(text, isNick);
      if (parser.matches())
      {
          String[] data = parser.getParsedStrings();
          eventInformation.put($NICK$,    data[0]);
          eventInformation.put($SOURCE$,  data[0]);
      }

      return eventInformation;
   }
}
