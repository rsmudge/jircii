package rero.client.listeners;

import rero.net.*;
import rero.net.interfaces.*;

import rero.ircfw.interfaces.ChatListener;

import java.util.*;

import rero.bridges.event.*;

public class _SocketDataListener extends ScriptedEventListener implements SocketDataListener
{
   protected SocketConnection socket;

   public _SocketDataListener(SocketConnection _socket)
   {
      socket = _socket;
   }

   public void socketDataRead(SocketEvent ev)
   {
      HashMap eventData = new HashMap();
      eventData.put("$parms", ev.message);
      eventData.put("$data", ev.data.hostname + " " + ev.message);
      eventData.put("$server", ev.data.hostname);
      eventData.put("$port", ev.data.port + "");

      if ((dispatchEvent(eventData) & ChatListener.EVENT_HALT) == ChatListener.EVENT_HALT)
      {
         ev.valid = false;
      }
   }

   public void setupListener()
   {
      socket.addSocketDataListener(this);
   }
}
