package rero.ircfw.interfaces;

import java.util.HashMap;

public interface ChatListener
{
   public static final int EVENT_DONE      = 1;
   public static final int EVENT_HALT      = 2;
   public static final int REMOVE_LISTENER = 4;

   public int fireChatEvent (HashMap eventDescription); 

   public boolean isChatEvent(String eventId, HashMap eventDescription);
}
