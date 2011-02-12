package rero.bridges.event;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import java.util.*;
import rero.util.ClientUtils;

public class GeneralChatListener extends EventChatListener
{
    protected String eventId;

    public GeneralChatListener(String id)
    {
       eventId = id.toUpperCase();
    }

    public boolean isChatEvent(String _eventId, HashMap eventDescription)
    {
       return eventId.equals(_eventId.toUpperCase());
    }
}
