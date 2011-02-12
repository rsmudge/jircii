package rero.bridges.event;

import java.util.regex.*;
import java.util.*;
import rero.util.*;
import sleep.engine.atoms.*;
import sleep.runtime.*;

import rero.script.*;

public class FilterChatListener extends EventChatListener
{
   protected ScriptEnvironment env;
   protected String[]          filter;
   protected String            event;
   protected CodeSnippet       code;
   protected int               isPublic; // 0 we don't care, 1 public only, 2 private only

   private static final Pattern filterPattern = Pattern.compile("(.*?) (.*?) (.*)");

   public FilterChatListener(ScriptEnvironment _env, String _event, String _filter, CodeSnippet _code)
   {
      env    = _env;
      event  = _event.toUpperCase();

      if (event.equals("PUBLIC"))
      {
         isPublic = 1;
         event    = "PRIVMSG";
      }
      else if (event.equals("PUBLIC_ACTION"))
      {
         isPublic = 1;
         event    = "ACTION";
      }
      else if (event.equals("PRIVATE_ACTION"))
      {
         isPublic = 2;
         event    = "ACTION";
      }
      else if (event.equals("MSG"))
      {
         isPublic = 2;
         event    = "MSG";
      }
      else if (event.length() > 6 && event.substring(0, 5).equals("REPL_"))
      {
         isPublic = 0;
         event    = event.substring(5, event.length());
      }
      else
      {
         isPublic = 0;
      }

      code   = _code;

      //
      // evaluate the specified filter and then split up the result into something we can use...
      //
      filter  = new String[3];
      _filter = (env.evaluateExpression(_filter) + "");

      Matcher matcher = filterPattern.matcher(_filter.toUpperCase());

      if (matcher.matches())
      {
         filter[0] = matcher.group(1);
         filter[1] = matcher.group(2);
         filter[2] = matcher.group(3);
         addListener(_code);
      }      
      else
      {
         _env.getScriptInstance().fireWarning("Invalid format for event " + _event + " filter: " + _filter, _code.getLineNumber());
      }
   }

   private boolean isMatch(HashMap eventDescription)
   {
      String target, parms, from;
      target = (eventDescription.get("$target") + "").toUpperCase();
      from   = (eventDescription.get("$source") + "").toUpperCase();
      parms  = (eventDescription.get("$parms") + "").toUpperCase();

      if (target.equals("")) { target = "<none>"; }
      if (from.equals("")) { target = "<none>"; }

      boolean check = true;

      if (isPublic > 0 && ClientUtils.isChannel(target))
      {
         check = isPublic == 1;
      }

      return check && StringUtils.iswm(filter[0], from) && 
                      StringUtils.iswm(filter[1], target) && 
                      StringUtils.iswm(filter[2], parms);
   }

   public boolean isChatEvent(String eventId, HashMap eventDescription)
   {
      if (!code.isValid())
      {
         return false;
      }

      return (eventId.toUpperCase().equals(event) && isMatch(eventDescription));
   }
}
