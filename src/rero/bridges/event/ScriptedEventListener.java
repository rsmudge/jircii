package rero.bridges.event;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import rero.bridges.event.CodeSnippet;
import rero.ircfw.interfaces.ChatListener; // mainly for the constants.

public abstract class ScriptedEventListener
{
    protected boolean registered = false;  // an indicator of wether or not this listener has been setup.

    protected List temporary = new LinkedList();
    protected List permanent = new LinkedList();       

    public int dispatchEvent(HashMap eventDescription)
    {
        int rv = easyDispatch(temporary, eventDescription);

        if (rv == ChatListener.EVENT_DONE)
        {
            rv = easyDispatch(permanent, eventDescription);
        }
 
        return rv;
    }

    private int easyDispatch(List listeners, HashMap eventDescription)
    {
        CodeSnippet l;

        ListIterator iter = listeners.listIterator();
        while (iter.hasNext())
        {
            l = (CodeSnippet)iter.next();
   
            if (l.isValid())            
            {
                if (listeners == temporary)
                {
                    iter.remove();
                }

                int rv = l.execute(eventDescription);

                if ((rv & (ChatListener.REMOVE_LISTENER)) == ChatListener.REMOVE_LISTENER && listeners != temporary)
                {
                    iter.remove();
                }

                if ((rv & (ChatListener.EVENT_HALT)) == ChatListener.EVENT_HALT)
                {
                    return ChatListener.EVENT_HALT;
                }
            }
            else
            {
                iter.remove(); // get rid of old events
            }
        }
       
        return ChatListener.EVENT_DONE;
    }

    public void addTemporaryListener(CodeSnippet c)
    {
        temporary.add(c);
    }

    public void addListener(CodeSnippet c)
    {
        permanent.add(c);
    }

    public abstract void setupListener();

    public boolean isSetup()
    {
        return registered;
    }

    public void setRegistered() 
    {
        registered = true;
    }
}


