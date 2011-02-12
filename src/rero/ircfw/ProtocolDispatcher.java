package rero.ircfw;

import rero.ircfw.interfaces.FrameworkConstants;
import rero.ircfw.interfaces.ChatListener;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

public class ProtocolDispatcher implements FrameworkConstants
{
    protected LinkedList temporary = new LinkedList();
    protected LinkedList permanent = new LinkedList();       

    protected ChatListener internal = null;

    public void dispatchEvent(HashMap eventDescription)
    {
        String eventId = (String)eventDescription.get($EVENT$);

        int rv = ChatListener.EVENT_DONE;

        if (internal != null && internal.isChatEvent(eventId, eventDescription))
        {
            rv = internal.fireChatEvent(eventDescription);
        }

        if (rv == ChatListener.EVENT_DONE)
        {
            rv = easyDispatch(temporary, eventId, eventDescription);
        }

        if (rv == ChatListener.EVENT_DONE)
        {
            rv = easyDispatch(permanent, eventId, eventDescription);
        }
    }

    private int easyDispatch(List listeners, String eventId, HashMap eventDescription)
    {
        ChatListener l;

        ListIterator iter = listeners.listIterator();
        while (iter.hasNext())
        {
            l = (ChatListener)iter.next();
   
            if (l.isChatEvent(eventId, eventDescription))            
            {
                int rv = l.fireChatEvent(eventDescription);

                if ((rv & (ChatListener.REMOVE_LISTENER)) == ChatListener.REMOVE_LISTENER)
                {
                    iter.remove();
                }

                if ((rv & (ChatListener.EVENT_HALT)) == ChatListener.EVENT_HALT)
                {
                    return ChatListener.EVENT_HALT;
                }
            }
        }
       
        return ChatListener.EVENT_DONE;
    }

    public void addTemporaryListener(ChatListener l)
    {
        temporary.addFirst(l);
    }

    public void addChatListener(ChatListener l)
    {
        permanent.addFirst(l);
    }

    public void setInternalListener(ChatListener l)
    {
        internal = l;
    }
}


