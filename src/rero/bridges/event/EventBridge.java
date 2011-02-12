package rero.bridges.event;
 
import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import sleep.engine.atoms.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

/** Man, I feel bad for the mantainer of this class.  Oh wait thats me.  Actually its not that bad. 
    However this class isn't just as straight forward as registering any kind of event and going
    with it one way.  Nope, there are 3 ways events can be handled.

    <pre>
    1. normal chat event i.e. on JOIN for a channel join
       this is handled by adding the code to a collapsed listener (i.e. one real listener attached to
       the framework for the JOIN event).  If the collapsed listener does not exist it is created and
       registered with the irc framework.

    2. temporary chat event i.e. on PART for a channel part
       temporary chat events are handled by just creating a real listener and attaching it to the framework
       as a temporary listener.  No muss, no fuss.

    3. "registered" listeners i.e. on WINDOW
       registered listeners are listeners that aren't really IRC events.  They have there own framework
       for dealing with themselves.  There is a super class ScriptedEventListener that provides the interface
       the event bridge works with.  Other frameworks have a listener class that extends ScriptedEventListener
       and know to fire dispatchEvent when the listener is fired.  This should be more efficient as it keeps
       overhead low on some high occurence events (i.e. on WINDOW) and keeps the framework from being mucked
       up with tons of events.  All valuable things right.
    </pre>
**/

public class EventBridge implements Loadable, Environment, PredicateEnvironment, FilterEnvironment
{
//    public    ScriptEnvironment environment;
    protected HashMap           listeners;
    protected ChatFramework     framework;

    protected HashMap           registeredEvents;

    protected HashMap           unloadEvents;

    public EventBridge()
    {
        listeners = new HashMap();
        registeredEvents = new HashMap();
        unloadEvents = new HashMap();
    }
  
    public void announceFramework(ChatFramework f)
    {
        framework = f;
    }

    public void scriptUnloaded (ScriptInstance si)
    {
        if (unloadEvents.get(si) != null)
        {
           Block code = (Block)unloadEvents.get(si);
           unloadEvents.remove(si);

           synchronized (si.getScriptEnvironment().getScriptVariables())
           {
              si.getScriptEnvironment().getScriptVariables().pushLocalLevel();
              SleepUtils.runCode(code, si.getScriptEnvironment());
              si.getScriptEnvironment().getScriptVariables().popLocalLevel();
           }
        }
    }

    public void scriptLoaded (ScriptInstance si)
    {
        Hashtable _env = si.getScriptEnvironment().getEnvironment();
        _env.put("on",   this); // since we implement both interfaces this should be okay.
        _env.put("wait", this); 

//        environment = si.getScriptEnvironment();
    }

    /** adds another type of "event" for the event bridge to manage.  By default events are just registered with the irc 
        framework.  ScriptedEventListener allows any sort of event to be incorporated into the client scripting */
    public void registerEvent(String name, ScriptedEventListener listener)
    {
        registeredEvents.put(name.toUpperCase(), listener);
    }

    protected EventChatListener getNewListenerFor(String event_name)
    {
        if (event_name.equals("PUBLIC"))
        {
           return new PublicChatListener("PRIVMSG");
        }

        if (event_name.equals("PUBLIC_ACTION"))
        {
           return new PublicChatListener("ACTION");
        }

        if (event_name.equals("PRIVATE_ACTION"))
        {
           return new PrivateChatListener("ACTION");
        }

        if (event_name.equals("MSG"))
        {
           return new PrivateChatListener("PRIVMSG");
        }

        if (event_name.length() > 6 && event_name.substring(0, 5).equals("REPL_"))
        {
           return new GeneralChatListener(event_name.substring(5, event_name.length()));
        } 

        return new GeneralChatListener(event_name);
    }

    //
    // returns a "collapsed" listener.  Just trying to reduce the overhead of firing an event.
    //
    protected EventChatListener getListenerFor(String event_name)
    {
        if (listeners.get(event_name) == null)
        {
           listeners.put(event_name, getNewListenerFor(event_name));
           framework.addChatListener((ChatListener)listeners.get(event_name));
        }

        return (EventChatListener)listeners.get(event_name);
    }

    public void bindFilteredFunction(ScriptInstance si, String typeKeyword, String keyword, String filter, Block functionBody)
    {
        FilterChatListener temp = new FilterChatListener(si.getScriptEnvironment(), keyword, filter, new CodeSnippet(functionBody, si.getScriptEnvironment()));

        if (typeKeyword.equals("on"))
        {
           framework.addChatListener((ChatListener)temp);
        }

        if (typeKeyword.equals("wait"))
        {
           temp.setFlags(ChatListener.REMOVE_LISTENER);
           framework.addTemporaryListener(temp);
        }
    }    

    public void bindPredicate(ScriptInstance si, String type, Check pred, Block code)
    {
        PredicateChatListener temp = new PredicateChatListener(si.getScriptEnvironment(), pred, new CodeSnippet(code, si.getScriptEnvironment()));

        if (type.equals("on"))
        {
           framework.addChatListener((ChatListener)temp);
        }

        if (type.equals("wait"))
        {
           temp.setFlags(ChatListener.REMOVE_LISTENER);  // tells it to flag all temp events as removeable.
           framework.addTemporaryListener(temp);
        }
    }

    public void bindFunction(ScriptInstance si, String type, String name, Block code)
    {
        name = name.toUpperCase();

        if (type.equals("on") && name.equals("UNLOAD"))
        {
           unloadEvents.put(si, code);           
        }
        else if (registeredEvents.get(name) != null)
        {
           ScriptedEventListener temp = (ScriptedEventListener)registeredEvents.get(name);
           if (!temp.isSetup())
           {
              temp.setupListener();
              temp.setRegistered();
           }
           
           if (type.equals("on"))
           {
              temp.addListener(new CodeSnippet(code, si.getScriptEnvironment()));
           }

           if (type.equals("wait"))
           {
              temp.addTemporaryListener(new CodeSnippet(code, si.getScriptEnvironment()));
           }

           return;
        }
        else if (type.equals("on"))
        {
           // when the listener is created it is added to the umm thingy.
           EventChatListener temp = getListenerFor(name);
           temp.addListener(new CodeSnippet(code, si.getScriptEnvironment()));
        }
        else if (type.equals("wait"))
        {
           EventChatListener temp = getNewListenerFor(name);
           temp.addListener(new CodeSnippet(code, si.getScriptEnvironment()));
           temp.setFlags(ChatListener.REMOVE_LISTENER);  // tells it to flag all temp events as removeable.

           framework.addTemporaryListener(temp);
        }
    }
}

