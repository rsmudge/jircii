package rero.client;

import rero.net.SocketSystem;
import rero.ircfw.ChatFramework;

import java.util.*;
import java.io.*;

import rero.net.SocketInformation;

import rero.net.SocketEvent;
import rero.net.interfaces.SocketStatusListener;

import rero.ircfw.interfaces.ChatListener;
import rero.ircfw.interfaces.FrameworkConstants;

import rero.script.*;
import sleep.runtime.*;

import rero.dcc.*;

import rero.util.*;

import rero.client.user.*;     // import features
import rero.client.server.*;
import rero.client.notify.*;
import rero.client.dcc.*;   
import rero.client.listeners.*; // misc. built in client events.  fun fun.
import rero.client.output.*;
import rero.client.data.*;
import rero.client.script.*;

import rero.gui.*;
import rero.config.*;

public class InternetRelayChatClient
{
   protected WeakHashMap   data  = new WeakHashMap();   // Client central data repository.  

   //
   // Frameworks and Such
   //
   protected ChatFramework ircfw = new ChatFramework();
   protected SocketSystem  sock  = new SocketSystem();
   protected ScriptCore   script = new ScriptCore();

   protected Capabilities  actions;

   public void init(UICapabilities gui)
   {
       ircfw.storeDataStructures(data);
        sock.storeDataStructures(data);
      script.storeDataStructures(data);

      //
      // import systems into eachother.
      //
      script.announceFramework(ircfw);  // this has to be done early on to get the bridges to be created.

      sock.addSocketDataListener(ircfw.getProtocolHandler()); /* socket events are fired in a first in first out fashion.
                                                              /* so the framework will be the last thing to touch the socket
                                                              /* event... */

      actions = new Capabilities(ircfw, sock.getSocket(), script, gui, data);
   }

   public void post()
   {
      //
      // do other fun stuff... i.e. script loading and such
      //
      ((ScriptManager)getCapabilities().getDataStructure(DataStructures.ScriptManager)).loadScripts();
   }

   public Capabilities getCapabilities()
   {
      return actions;
   }

   public WeakHashMap getData() 
   { 
      return data; 
   }
}
