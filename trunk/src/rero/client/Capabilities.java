package rero.client;

import rero.net.*;
import rero.ircfw.*;
import rero.ircfw.interfaces.*;
import rero.script.*;
import rero.util.*;
import rero.dcc.*;

import java.util.*;
import java.io.*;

import sleep.runtime.*;

import rero.client.user.*;     // import features
import rero.client.server.*;
import rero.client.notify.*;
import rero.client.dcc.*;
import rero.client.listeners.*; // misc. built in client events.  fun fun.
import rero.client.output.*;
import rero.client.data.*;
import rero.client.script.*;

import rero.client.functions.*; // built in functions

import rero.gui.*;

/** capabilities that feature classes may want to take advantage of */
public class Capabilities
{
    protected SocketConnection   socket;
    protected ChatFramework      ircfw;
    protected ScriptCore         script;
    protected OutputCapabilities output;
    protected ChatCapabilities   actions;

    protected rero.gui.UICapabilities gui;

    protected static TimerUtil   timer;  // heres an important capability... yeah we *might* want access to this.

    protected WeakHashMap        data;
    protected LinkedList         featureList;

    public Capabilities(ChatFramework _ircfw, SocketConnection _socket, ScriptCore _script, UICapabilities _gui, WeakHashMap _data)
    {
        //
        // setup some of the stored stuff.
        //
        ircfw   = _ircfw;
        socket  = _socket;
        script  = _script;
        gui     = _gui;

        data  = _data;

        if (timer == null)
        {
           timer = new TimerUtil();
           timer.start();
        }

        //
        // setup all of the built in features...
        //
        featureList = new LinkedList();

        output = new OutputCapabilities();
        setupFeature(output, featureList);

        actions = new ChatCapabilities();
        setupFeature(actions, featureList);

        UserHandler userInput = new UserHandler();         // handle user input, aliases, and active stuff
        setupFeature(userInput, featureList);

        ServerHandler serverOutput = new ServerHandler();  // handle output from the server feature(s)
        setupFeature(serverOutput, featureList);

        FeatureDCC dcc = new FeatureDCC();                 // built in dcc stuff, fun fun fun
        setupFeature(dcc, featureList);

        ProcessEvents processEvents = new ProcessEvents();       // process some basic sets from the server i.e. DCC before everyone e$
        setupFeature(processEvents, featureList);

        NotifyData notify = new NotifyData();              // notify feature
        setupFeature(notify, featureList);

        InternalEvents internalEvents = new InternalEvents(); // built in events, non framework related
        setupFeature(internalEvents, featureList);

        LocalInfo localInfo = new LocalInfo();             // local host information so dcc's go through properly.  whee. 
        setupFeature(localInfo, featureList);
    
        ScriptManager scriptManager = new ScriptManager();  // load the script manager
        setupFeature(scriptManager, featureList);

        PerformOnConnect performConnect = new PerformOnConnect(); // stuff for performing commands upon connecting
        setupFeature(performConnect, featureList);

        //
        // scripting related features
        //
        DataStructureBridge dataStructs = new DataStructureBridge(); // bridges some of the data structures to the scripting
        setupFeature(dataStructs, featureList);
 
        ChannelOperators chanops = new ChannelOperators(); // channel operators i.e. ison isop etc..
        setupFeature(chanops, featureList);
        script.addBridge(chanops);

        UserOperators userops = new UserOperators(); // user related operators/functions etc. 
        setupFeature(userops, featureList);
        script.addBridge(userops);

        BuiltInOperators builtinops = new BuiltInOperators(); // built in commands and such
        setupFeature(builtinops, featureList);
        script.addBridge(builtinops);

        NotifyOperators notifyops = new NotifyOperators(); // notify related stuff..
        setupFeature(notifyops, featureList);
        script.addBridge(notifyops);

        TimerOperators timerops = new TimerOperators(); // timer related stuff..
        setupFeature(timerops, featureList);

        ConfigOperators configops = new ConfigOperators(); // interface with the client state
        setupFeature(configops, featureList);

        DCCOperators dccops = new DCCOperators(); // interface with the dcc code... *uNF*
        setupFeature(dccops, featureList);

        UtilOperators utilops = new UtilOperators(); // interface with the utility functions
        setupFeature(utilops, featureList);

        TokenOperators tokenops = new TokenOperators(); // interface with the token functions
        setupFeature(tokenops, featureList);

        ServerOperators serverops = new ServerOperators(); // interface with the servers.ini data
        setupFeature(serverops, featureList);

        SoundOperators soundops = new SoundOperators(); // add sound playing functions to jIRCii
        setupFeature(soundops, featureList);

        finalizeFeatures(featureList);

        //
        // do other fun stuff... i.e. script loading and such
        //
    }

    public TimerUtil getTimer()
    {
        return timer;
    }

    public void cleanup()
    {
        ListIterator i = featureList.listIterator();
        while (i.hasNext())
        {
           Feature temp = (Feature)i.next();
           temp.cleanup();
        }

        data.clear();
        featureList.clear();
    }

    public boolean isConnected()
    {
        return socket.getSocketInformation().isConnected; // yeah the socket stuff is goofy, my bad.
    }

    public ChatCapabilities getChatCapabilities()
    {
        return actions;
    }

    public OutputCapabilities getOutputCapabilities()
    {
        return output;
    }

    public rero.gui.UICapabilities getUserInterface()
    {
        return gui;
    }

    public ScriptCore getScriptCore()
    {
        return script;
    }

    public SocketConnection getSocketConnection()
    {
        return socket;
    }

    public void sendln(String text)
    {
        socket.println(text);
    }

    public Object getDataStructure(String key)
    {
        return data.get(key);
    }

    public void injectEvent(String text)
    {
        ircfw.injectEvent(text);
    }

    public void dispatchEvent(HashMap data)
    {
        ircfw.getProtocolDispatcher().dispatchEvent(data);
    }

    public void addChatListener(ChatListener l)
    {
        ircfw.addChatListener(l);
    }

    public void addTemporaryListener(ChatListener l)
    {
        ircfw.addTemporaryListener(l);
    }

    public ChatFramework getChatFramework()
    {
        return ircfw;
    }

    public void registerCommand(String command, ClientCommand code)
    {
        UserHandler commands = (UserHandler)getDataStructure("commands");
        commands.registerCommand(command, code);
    }

    public void setupFeature(Feature aFeature, LinkedList features)
    {
        aFeature.storeDataStructures(data);
        aFeature.installCapabilities(this);
 
        features.add(aFeature);
    }
 
    public void finalizeFeatures(LinkedList features)
    {
        ListIterator i = features.listIterator();
        while (i.hasNext())
        {
           Feature temp = (Feature)i.next();
           temp.init();
        }
    }

    public GlobalCapabilities getGlobalCapabilities()
    {
        return SessionManager.getGlobalCapabilities();
    }
}
