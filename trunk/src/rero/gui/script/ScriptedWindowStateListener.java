package rero.gui.script;

import rero.client.output.*;
import java.util.*;

import rero.ircfw.interfaces.ChatListener;

import rero.gui.*;
import rero.gui.windows.*;

import rero.bridges.event.*;

public class ScriptedWindowStateListener extends ScriptedEventListener
{
   protected IRCSession          gui;

   public ScriptedWindowStateListener(IRCSession _gui)
   {
      gui    = _gui;
   }

   public void onWindowEvent(ClientWindowEvent ev)
   {
      HashMap event = new HashMap();

      event.put("$window", gui.resolveClientWindow(ev.getSource()).getName());
      dispatchEvent(event);
   }

   public void setupListener()
   {
      // do nothing, this will be installed by default (unfortunately)
   }
}
