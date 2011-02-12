package rero.bridges.event;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import java.util.*;
import rero.util.ClientUtils;

public class PublicChatListener extends EventChatListener
{
    protected String eventId;

    public PublicChatListener(String id)
    {
       eventId = id.toUpperCase();
    }

    public boolean isChatEvent(String _eventId, HashMap eventDescription)
    {
       String target = (String)eventDescription.get(FrameworkConstants.$TARGET$);
       return eventId.equals(_eventId.toUpperCase()) && ClientUtils.isChannel(target);
    }
}
