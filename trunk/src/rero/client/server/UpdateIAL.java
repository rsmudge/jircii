package rero.client.server;

import rero.ircfw.interfaces.*;
import java.util.*;

public class UpdateIAL implements ChatListener
{
   public int fireChatEvent(HashMap eventDescription)
   {
       String event = (String)eventDescription.get("$event");

       if (event.equals("315"))
       {
          return REMOVE_LISTENER | EVENT_HALT;  // end of a /who reply we got what we wanted
       }
       else if (event.equals("352"))
       {
          return EVENT_HALT; // we're still getting the reply, so ignore it for now.
       }

       return EVENT_DONE;
   }

   public boolean isChatEvent(String event, HashMap eventDescription)
   {
       if (event.equals("315")) { return true; } /* End of /WHO reply */
       if (event.equals("352")) { return true; } /* /WHO reply */

       return false;
   }
}
