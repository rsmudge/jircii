package rero.gui.sdi;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import rero.gui.windows.*;

public class ClientSingleWindow extends JPanel implements ClientWindow
{
   protected LinkedList        clisteners = new LinkedList();
   protected ClientWindowEvent cevent;
   protected ClientPanel       parent;

   public ClientSingleWindow(ClientPanel _parent)
   {
      cevent = new ClientWindowEvent(this);
      setLayout(new BorderLayout());

      parent = _parent;
   }

   public void addWindowListener(ClientWindowListener l)
   {
      clisteners.add(l);
   }
 
   public void processActive()
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
         ClientWindowListener temp = (ClientWindowListener)i.next();
         temp.onActive(cevent);
      } 
   }              

   public void processInactive()
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
         ClientWindowListener temp = (ClientWindowListener)i.next();
         temp.onInactive(cevent);
      } 
   }

   public void processClose()
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
          ClientWindowListener temp = (ClientWindowListener)i.next();
          temp.onClose(cevent);
      } 
   }              

   public void processOpen()
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
          ClientWindowListener temp = (ClientWindowListener)i.next();
          temp.onOpen(cevent);
      } 
   }              

    public void closeWindow()
    {
       parent.killWindow(this);
    }

    public boolean isMaximum() { return false; }
    public boolean isIcon() { return false; }

    public void setMaximum(boolean b) { }

    public void setIcon(boolean b) 
    {
        if (b)
        {
           parent.doDeactivate(parent.getWindowFor(this));
        }
        else
        {
           parent.doActivate(parent.getWindowFor(this));
        }
    }

    public void setIcon(ImageIcon blah) { }
    public void show() { }

    public boolean isSelected() { return parent.getActiveWindow() != null && parent.getActiveWindow().getWindow() == this; }

    public void activate() 
    { 
        parent.doActivate(parent.getWindowFor(this));
    }


    public void setTitle(String title) { }
    public String getTitle() { return ""; }

    public void setContentPane(Container c)
    {
       add(c, BorderLayout.CENTER);
    }
}
