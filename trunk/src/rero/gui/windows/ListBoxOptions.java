package rero.gui.windows;

import java.awt.*;
import javax.swing.*;

import rero.config.*;

public class ListBoxOptions implements ClientStateListener
{
   protected JComponent container;
   protected JComponent listbox;

   public ListBoxOptions(JComponent c, JComponent l)
   {
      container = c;
      listbox   = l;

      ClientState.getClientState().addClientStateListener("listbox.position", this);
      ClientState.getClientState().addClientStateListener("listbox.enabled", this);

      rehash();
   }

   public void rehash()
   {
      synchronized (listbox)
      {
         container.remove(listbox);

         boolean enabled  = ClientState.getClientState().isOption("listbox.enabled", true);
         int     position = ClientState.getClientState().getInteger("listbox.position", 1); // default to right...

         if (enabled)
         {
            if (position == 0)
            {
               container.add(listbox, BorderLayout.WEST);
            }
            else
            {
               container.add(listbox, BorderLayout.EAST);
            }
         }
      }
   }

   public void propertyChanged(String key, String value)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            rehash();
            container.revalidate();
         } 
      });
   }
}

