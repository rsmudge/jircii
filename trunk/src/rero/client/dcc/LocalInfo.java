package rero.client.dcc;

import rero.dcc.*;

import rero.client.*;
import rero.client.user.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import rero.util.*;

import java.io.*;
import java.util.*;

import rero.gui.*;
import rero.dialogs.dcc.*;

import rero.config.*;

import java.net.*;

public class LocalInfo extends Feature implements FrameworkConstants, ClientStateListener
{
   public static final String RESOLVE_FROM_SERVER = "Server Method";
   public static final String RESOLVE_AUTOMATIC   = "Normal Method";

   protected static String localHost = null;

   public void setLocalHost(String host)
   {
      try
      {
         if (localHost == null)
         {
            localHost   = InetAddress.getByName(host).getHostAddress();
            resolveHost = localHost;

            HashMap eventDescription = new HashMap();
            eventDescription.put("$data", localHost + " " + localHost);
            eventDescription.put("$parms", localHost);
            getCapabilities().getOutputCapabilities().fireSetStatus(eventDescription, "RESOLVED_LOCALINFO");      
         }
      }
      catch (Exception ex)
      {
         localHost        = null;
         resolveProcessed = false;
         resolveHost      = null;
         ex.printStackTrace();
      }
   }

   public String localip()
   {
      if (localHost != null)
      {
         return localHost;
      }

      try
      {
         localHost = InetAddress.getLocalHost().getHostAddress();
         return localHost;
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }

      return "127.0.0.1";
   }


   public void storeDataStructures(WeakHashMap data)
   {
      data.put("localInfo", this);
   }

   public void init()
   {
      ClientState.getClientState().addClientStateListener("dcc.localinfo", this);
      processLocalInfoSettings();
   }

   protected static boolean resolveProcessed = false; // to make sure resolve doesn't get process twice
   protected static String  resolveHost      = null;  // save the host if we have to...

   public void processLocalInfoSettings()
   {
      if (localHost != null)  // localhost has already been processed... f00.
         return;

      if (ClientState.getClientState().getString("dcc.localinfo", ClientDefaults.dcc_localinfo).equals(RESOLVE_AUTOMATIC))
      {
         (new Thread(new Runnable() { public void run() { localip(); } })).start();
      }
      else if (ClientState.getClientState().getString("dcc.localinfo", ClientDefaults.dcc_localinfo).equals(RESOLVE_FROM_SERVER))
      {
         if (! resolveProcessed)
         {
            getCapabilities().addTemporaryListener(new ResolveLocalInfo()); 
         }
         else
         {
            localHost = resolveHost;            
         }
      }
      else
      {
         localHost = ClientState.getClientState().getString("dcc.localinfo", "127.0.0.1");
      }
   }

   public void propertyChanged(String property, String parameter)
   {
      localHost = null;
      processLocalInfoSettings();
   }

   protected class ResolveLocalInfo implements ChatListener, FrameworkConstants
   {
      protected String myhost;

      public ResolveLocalInfo()
      {
         if (getCapabilities().isConnected())
         {
            getCapabilities().sendln("USERHOST " + ((InternalDataList)getCapabilities().getDataStructure(DataStructures.InternalDataList)).getMyNick());
         }
      }

      public int fireChatEvent(HashMap eventDescription)
      {
          if (eventDescription.get($EVENT$).equals("302"))
          {
             myhost = eventDescription.get("$address").toString();
             myhost = myhost.substring(myhost.indexOf('@') + 1, myhost.length());
 
             new Thread(new Runnable()
             {
                public void run()
                {               
                   setLocalHost(myhost);    
                }
             }).start();

             resolveProcessed = true;   // do this so we don't have to ask to resolve local info via the server again.
             // resolveHost will be set by the setLocalHost() function...

             return REMOVE_LISTENER | EVENT_HALT;  // end of a /who reply we got what we wanted
          }
          else if (eventDescription.get($EVENT$).equals("376"))
          {
             getCapabilities().sendln("USERHOST " + ((InternalDataList)getCapabilities().getDataStructure(DataStructures.InternalDataList)).getMyNick());
          }

          return EVENT_DONE;
      }

      public boolean isChatEvent(String event, HashMap eventDescription)
      {
          return event.equals("302") || event.equals("376"); // /USERHOST reply
      }
   }
}
