package rero.gui.windows;

import rero.ircfw.*;
import rero.client.*;
import javax.swing.*;

import rero.config.*;

public class QueryWindow extends StatusWindow
{
   protected String   user; // user object.

   public QueryWindow(String _user)
   {
      user = _user;
   }

   public ImageIcon getImageIcon()
   {
      if (icon == null)
      {
         icon = new ImageIcon(ClientState.getClientState().getResource("query.gif"));
      }

      return icon;
   }

   public void installCapabilities(Capabilities c)
   {
      super.installCapabilities(c);
   }

   public String getWindowType()
   {
      if (user.charAt(0) == '=')
      {
         return "chat";
      }

      return "query";
   }

   public String getQuery()
   {
      return user;
   }

   public void setName(String name)  
   {
      user = name;
      super.setName(name);
   }

   public String getName()
   {
      return user;
   }

   public int compareWindowType()
   {
      return 3;
   }
}
