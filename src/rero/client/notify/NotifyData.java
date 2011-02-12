package rero.client.notify;

import java.util.*;

import rero.client.*;
import rero.util.*;

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import rero.config.*;

//
// Documented bug - if there is no notify reply before the next one is sent and then both come one of them
//   will be echo'd.   Its not really worth working around this problem.  
//
public class NotifyData extends Feature implements ChatListener, TimerListener, ClientStateListener
{
   protected HashMap users     = new HashMap();
   protected Set     signedon  = new HashSet();
   protected Lag     lag       = new Lag();

   protected int isChecking = 0;

   public void reset()
   {
      signedon = new HashSet();
   }

   public void propertyChanged(String value, String parameter)
   {
      hashUsers();    
   }

   public void hashUsers()
   {
      HashMap newUsers = new HashMap();

      Iterator i = ClientState.getClientState().getStringList("notify.users").getList().iterator();
      while (i.hasNext())
      {
         String temp = (String)i.next();
         if (users.containsKey(temp))
         {
            newUsers.put(temp, users.get(temp));
         }         
         else
         {
            newUsers.put(temp, createNotifyUser(temp));
         }
      }

      users = newUsers;
   }

   public void addUser(String nickname) // *permanently adds user to notify list*
   {
      StringList temp = ClientState.getClientState().getStringList("notify.users");
      temp.add(nickname);
      temp.save();
      ClientState.getClientState().sync();
   }

   public void removeUser(String nickname)
   {
      StringList temp = ClientState.getClientState().getStringList("notify.users");
      temp.remove(nickname);
      temp.save();
      ClientState.getClientState().sync();
   }

   public Set getSignedOnUsers()
   {
      Set rv = new HashSet();

      Iterator i = users.values().iterator(); 
      while (i.hasNext())
      {
         NotifyUser temp = (NotifyUser)i.next();
         if (temp.isSignedOn())
         {
            rv.add(temp);
         }
      }
 
      return rv;
   }

   public Set getNotifyUsers()
   {
      return new HashSet(users.values());
   }

   public Set getSignedOffUsers()
   {
      Set rv = new HashSet();

      Iterator i = users.values().iterator(); 
      while (i.hasNext())
      {
         NotifyUser temp = (NotifyUser)i.next();
         if (!temp.isSignedOn())
         {
            rv.add(temp);
         }
      }
 
      return rv;
   }

   public void init()
   {
      hashUsers();

      getCapabilities().addChatListener(this);
      getCapabilities().getTimer().addTimer(this, 60 * 1000); // check the notify list every 60 seconds.

      ClientState.getClientState().addClientStateListener("notify.users", this);
   }

   public void cleanup()
   {
      getCapabilities().getTimer().stopTimer(this);
   }

   public void storeDataStructures(WeakHashMap data)
   {
      data.put("lag", lag);
      data.put("notify", this);
   }

   public NotifyUser createNotifyUser(String nickname)
   {
      NotifyUser temp = new NotifyUser(nickname);
      temp.installCapabilities(getCapabilities());

      return temp;
   }

   public NotifyUser getUserInfo(String nickname)
   {
      return (NotifyUser)users.get(nickname);
   }

   public void checkNotify() 
   {
      if (getCapabilities().isConnected())
      {
         isChecking++;

         StringBuffer temp = new StringBuffer("ISON :");
         Iterator i = users.keySet().iterator();
         while (i.hasNext())
         {
            temp.append((String)i.next());
            temp.append(" ");
         }

         lag.checkLag();
         getCapabilities().sendln(temp.toString());
      }
   }

   public void timerExecute()
   {
      checkNotify();
   }

   public int fireChatEvent (HashMap eventDescription)
   {
      if (eventDescription.get("$event").equals("376"))
      {
          // We are connected, check the notify
          //
          checkNotify();
          return EVENT_DONE;
      }

      lag.setLag();

      if (eventDescription.get("$event").equals("303"))
      {
         String parms = (String)eventDescription.get("$parms");

         TokenizedString temp = new TokenizedString(parms);
         temp.tokenize(" ");

         Set newbatch = new HashSet();

         for (int x = 0; x < temp.getTotalTokens(); x++)
         {
            NotifyUser user = getUserInfo(temp.getToken(x));

            if (user != null && !user.isSignedOn())
            {
               user.signOn();         
            }          
            newbatch.add(user);
         }

         signedon.removeAll(newbatch);

         Iterator i = signedon.iterator();
         while (i.hasNext())
         {
            NotifyUser user = (NotifyUser)i.next();
            if (user != null && user.isSignedOn() && users.containsKey(user.toString()))
            {
               user.signOff();
            }
         }

         signedon = newbatch;
      }

      isChecking--;

      return EVENT_HALT;
   }

   public boolean isChatEvent(String eventId, HashMap eventDescription)
   {
      // :lug.mtu.edu 303 ^butang :shrunk mutilator `butane
      return (isChecking > 0 && (eventId.equals("303") || eventId.equals("461"))) || eventId.equals("376");
   }
}
