package rero.client.server;

import rero.ircfw.interfaces.*;
import java.util.*;

import rero.util.*;

/** temporary listener to halt /list replies that don't match our criteria **/
public class ListFilter implements ChatListener
{
   protected String filter;

   public ListFilter(String _filter)
   {
       filter = _filter;
   }
  
   public int fireChatEvent(HashMap eventDescription)
   {
       String event = (String)eventDescription.get("$event");

       if (event.equals("323") || event.equals("416"))
       {
          return REMOVE_LISTENER | EVENT_HALT;  // end of a /list reply we got what we wanted
       }
       else if (event.equals("322") && !StringUtils.iswm(filter, eventDescription.get("$parms").toString()))
       {
          return EVENT_HALT; // we're still getting the reply, so ignore it for now.
       }

       return EVENT_DONE;
   }

   public boolean isChatEvent(String event, HashMap eventDescription)
   {
       if (event.equals("323") || event.equals("416")) { return true; } /* End of /LIST reply */
       if (event.equals("322")) { return true; } /* /LIST reply */

       return false;
   }
}
