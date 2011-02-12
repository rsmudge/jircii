package rero.client.listeners;

import rero.net.*;
import rero.net.interfaces.*;

import java.util.*;

import rero.bridges.event.*;

public class _SocketStatusListener extends ScriptedEventListener implements SocketStatusListener
{
   protected boolean          isConnectListener; // if false, we have a disconnect listener
   protected SocketConnection socket;

   public _SocketStatusListener(SocketConnection _socket, boolean connectListener)
   {
      socket = _socket;
      isConnectListener = connectListener;
   }

   public void socketStatusChanged(SocketEvent ev)
   {
      if (isConnectListener == ev.data.isConnected)
      {
          HashMap eventData = new HashMap();
          eventData.put("$parms", ev.message);
          eventData.put("$data", ev.data.hostname + " " + ev.message);
          eventData.put("$server", ev.data.hostname);
          eventData.put("$port", ev.data.port + "");

          dispatchEvent(eventData);
      } 
   }

   public void setupListener()
   {
      socket.addSocketStatusListener(this);
   }
}
