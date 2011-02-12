package rero.gui.mdi;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import rero.gui.windows.*;

public class ClientInternalWindow extends JInternalFrame implements ClientWindow, InternalFrameListener
{
   protected LinkedList        clisteners = new LinkedList();
   protected ClientWindowEvent cevent;
   protected boolean           isOpen = false;

   public ClientInternalWindow()
   {
      super("", true, true, true, true);

      cevent = new ClientWindowEvent(this);
      addInternalFrameListener(this);
   }

   public boolean isOpen()
   {
      return isOpen;
   }

   public void setIcon(ImageIcon i)
   {
      setFrameIcon(i);
   }

   public void addWindowListener(ClientWindowListener l)
   {
      clisteners.add(l);
   }

   public void internalFrameActivated(InternalFrameEvent e)
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
         ClientWindowListener temp = (ClientWindowListener)i.next();
         temp.onActive(cevent);
      } 
   }              

   public void internalFrameDeactivated(InternalFrameEvent e)
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
         ClientWindowListener temp = (ClientWindowListener)i.next();
         temp.onInactive(cevent);
      } 
   }              

   public void internalFrameClosed(InternalFrameEvent e)
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
          ClientWindowListener temp = (ClientWindowListener)i.next();
          temp.onClose(cevent);
      } 
   }              

   public void internalFrameClosing(InternalFrameEvent e) { }              

   public void internalFrameDeiconified(InternalFrameEvent e) { }              

   public void internalFrameIconified(InternalFrameEvent e)
   {
      Iterator i = clisteners.listIterator();
      while (i.hasNext())
      {
         ClientWindowListener temp = (ClientWindowListener)i.next();
         temp.onMinimize(cevent);
      } 
   }              

   public void internalFrameOpened(InternalFrameEvent e)
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
        SwingUtilities.invokeLater(new Runnable() 
        { 
            public void run()
            {
               try { setClosed(true); } catch (Exception ex) { }
            }
        });
    }

    public void setMaximum(boolean b)
    {
        try { super.setMaximum(b); } catch (Exception ex) { }
    }

    public void setIcon(boolean b)
    {
        try { super.setIcon(b); } catch (Exception ex) { }
    }

    public void setTitle(String aTitle)
    {
       title = aTitle;
       revalidate();
       repaint();
    }
  
    public void show()
    {
       isOpen = true;
       super.show();
    }


    public void activate()
    {
        SwingUtilities.invokeLater(new Runnable() 
        { 
           public void run()
           {
              try { 
                 if (!isOpen)
                    show(); 

                 setSelected(true); 
              } catch (Exception ex) { }
           }
        });
    }
}
