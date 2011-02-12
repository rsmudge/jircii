package rero.gui.script;

import rero.client.output.*;
import java.util.*;

import rero.ircfw.interfaces.ChatListener;

import rero.gui.*;
import rero.gui.windows.*;

import rero.bridges.event.*;

public class WindowStateListener implements ClientWindowListener
{
   protected IRCSession gui;

   protected ScriptedWindowStateListener active;
   protected ScriptedWindowStateListener inactive;
   protected ScriptedWindowStateListener close;
   protected ScriptedWindowStateListener open;
   protected ScriptedWindowStateListener minimize;

   public void registerListener(EventBridge bridge)
   {
       bridge.registerEvent("active",   active);
       bridge.registerEvent("inactive", inactive);
       bridge.registerEvent("close",    close);
       bridge.registerEvent("open",     open);
       bridge.registerEvent("minimize", minimize);
   }

   public void onActive(ClientWindowEvent ev)
   {
       active.onWindowEvent(ev);
   }

   public void onInactive(ClientWindowEvent ev)
   {
       inactive.onWindowEvent(ev);
   }

   public void onClose(ClientWindowEvent ev)
   {
       close.onWindowEvent(ev);
   }

   public void onOpen(ClientWindowEvent ev)
   {
       open.onWindowEvent(ev);
   }

   public void onMinimize(ClientWindowEvent ev)
   {
       minimize.onWindowEvent(ev);
   }

   public WindowStateListener(IRCSession _gui)
   {
       gui = _gui;
       active    = new ScriptedWindowStateListener(gui);
       inactive  = new ScriptedWindowStateListener(gui);
       close     = new ScriptedWindowStateListener(gui);
       open      = new ScriptedWindowStateListener(gui);
       minimize  = new ScriptedWindowStateListener(gui);
   }
}
