package rero.client.listeners;

import rero.ident.*;

import java.util.*;

import rero.bridges.event.*;

public class _IdentListener extends ScriptedEventListener implements IdentListener
{
   public _IdentListener()
   {
   }

   public void identRequest(String host, String text)
   {
       HashMap eventData = new HashMap();
       eventData.put("$event", "ident");
       eventData.put("$data" , host + " " + text);
       eventData.put("$parms", text);

       // are we using the protocol dispatcher?

       dispatchEvent(eventData);
   }

   public void setupListener()
   {
       IdentDaemon.getIdentDaemon().addIdentListener(this);
   }
}
