package rero.gui.windows;

import rero.ircfw.*;
import rero.ircfw.data.*;
import rero.ircfw.interfaces.*;

import text.list.*;
import text.*;
import rero.client.*;
import java.util.*;

import rero.gui.*;

/** I did not enjoy writing this code... really */
public class ChannelListData extends ListData 
{
   protected HashMap      userInfo;
   protected Channel      channel;

   protected Capabilities capabilities;

   protected HashMap      event;
   protected Iterator     tempIter;

   protected int          iterValue;

   public void dirty()
   {
      userInfo.clear();
   }

   public void removeUser(User u)
   {
      userInfo.remove(u);
   }

   public void installCapabilities(Capabilities c)
   {
      capabilities = c;
   }

   public void updateChannel(Channel _channel)
   {
      channel = _channel;

      event = new HashMap();
      event.put("$channel", channel.getName());

      userInfo = new HashMap();
   }

   public ChannelListData(Channel _channel)
   {
      updateChannel(_channel);
   }

   public int getSize()
   {
      if (getChannel() == null) { return 0; }
      return getChannel().getAllUsers().size();
   }

   public Channel getChannel()
   {      
      return channel;
   }

   public ListElement head()
   {
      if (getChannel() == null)
      {
         return null;
      }

      iterValue = getValue();
      tempIter = getChannel().getAllUsers().iterator();
      for (int x = 0; x < iterValue; x++)
      {
         tempIter.next();
      }
      return next();
   }
  
   public ListElement next()
   {
      if (getChannel() != null && tempIter.hasNext()) 
      {
         return getElementForUser((User)tempIter.next());
      }
      return null;
   }

   // in the listbox painting code we ensure we have the script variable lock first before doing any painting...
   public Object getSynchronizationKeyOuter()
   {
      if (capabilities != null)
      {
         // lock this tree by the script variables so no other thread can touch it...
         return capabilities.getDataStructure(DataStructures.ScriptVariables);
      }
      else
      {
         return getChannel().getAllUsers(); // prevent a null pointer exception when capabilities haven't been installed yet
      }
   }

   public Object getSynchronizationKeyInner()
   {
      return getChannel().getAllUsers();
   }

   protected ListElement getElementForUser(User u)
   {
      UserElement temp = (UserElement)userInfo.get(u);
      if (temp == null)
      {
         temp = new UserElement(u);
         userInfo.put(u, temp);
      }
      temp.touch();
      return temp;
   }

   public ListElement getElementAt(int number)
   {
      if (getChannel() == null) 
      {
         return null;
      }

      Iterator i = getChannel().getAllUsers().iterator();
      for (int x = 0; x < number && i.hasNext(); x++)
      {
         i.next();
      }

      if (i.hasNext())
      {
         User t = (User)i.next();
         return getElementForUser(t);
      }
      else
      {
         return null;
      }
   }

    public Iterator dataIterator()
    {
       return new MyIterator();
    }

    private class MyIterator implements Iterator
    {
       protected Iterator i;
  
       public MyIterator()
       {
          i = getChannel().getAllUsers().iterator();
       }

       public boolean hasNext()
       {
          return i.hasNext();          
       }

       public Object next()
       {
          return getElementForUser((User)i.next());
       }

       public void remove() { }
    }

    private class UserElement extends ListElement
    {
       protected int         oldState;  
       protected User        user;      
       protected AttributedString idle   = null;
       protected AttributedString normal = null;       
       protected int         oldNick;

       public UserElement(User _user)
       {
          user     = _user;
          oldState = _user.getModeFor(getChannel()); 
          oldNick  = _user.getNick().hashCode();

          setSource(user);
       }

       public void touch()
       {
          if (oldState != user.getModeFor(getChannel()) || oldNick != user.getNick().hashCode())
          {
             idle   = null;
             normal = null;
             oldNick  = user.getNick().hashCode();
             oldState = user.getModeFor(getChannel());
             setSelected(false);
          }
       }

       protected AttributedString buildString(String prepend)
       {
//          try
//          {
             if (capabilities != null) // some sort of weird race condition causes this to be null occasionally
             {
                event.put("$nick", user.getNick());
  
                AttributedString temp = AttributedString.CreateAttributedString(prepend + capabilities.getOutputCapabilities().parseSet(event, "NICKLIST_FORMAT"));
                temp.assignWidths();
                return temp;
             }
  /*        }
          catch (NullPointerException ex)
          {
             System.out.println("User:         " + user);
             System.out.println("prepend:      " + prepend);
             System.out.println("capabilities: " + capabilities);
             System.out.println("event:        " + event);
             System.out.println("output:       " + capabilities.getOutputCapabilities());
             System.out.println("getNick():    " + user.getNick());
          } */

          return null;
       }
 
       protected AttributedString getData()
       {
          if (user.getIdleTime() > (5 * 60))
          {
             if (idle == null)
             {
                idle = buildString("" + AttributedString.reverse);
             }
             return idle;              
          }
          else
          {
             if (normal == null)
             {
                normal = buildString("");
             }
             return normal;
          }
       }

       public AttributedText getAttributedText()
       {
          AttributedString data = getData();

          if (data != null)
             return getData().getAttributedText();

          AttributedString temp = AttributedString.CreateAttributedString("");
          temp.assignWidths();

          return temp.getAttributedText();
       }

       public String getText()
       { 
          return getData().getText();
       }
    }
}

