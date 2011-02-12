package rero.gui.windows;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import text.*;

import rero.client.*;
import rero.client.notify.*; // lag checking mechanism is part of the notify list implementation (it piggy backs off of it)

import javax.swing.border.*;

import rero.gui.*;
import rero.gui.background.*;
import java.util.*;

import rero.config.*;

public class WindowStatusBar extends BackgroundToolBar implements IRCAwareComponent, ChangeListener, ClientStateListener
{
   protected LabelDisplay contents;
   protected HashMap      event;
   protected StatusWindow parent;
   protected Capabilities capabilities;
   protected long         lastRehash;

   public void installCapabilities(Capabilities c) 
   {
       capabilities = c;       

       Lag temp = (Lag)capabilities.getDataStructure("lag");
       temp.addChangeListener(this);
   }

   public void stateChanged(ChangeEvent ev)
   {
       rehash();
       repaint();
   }

   public WindowStatusBar(StatusWindow _parent)
   {
      contents = new LabelDisplay();

      setFloatable(false);

      setLayout(new BorderLayout());
      add(contents, BorderLayout.CENTER);

      event = new HashMap();

      parent = _parent;

      setOpaque(false);

      setBorder(BorderFactory.createEmptyBorder(0, TextSource.UNIVERSAL_TWEAK, 0, TextSource.UNIVERSAL_TWEAK));
//      setBorder(null); 

      rehashValues();

      ClientState.getClientState().addClientStateListener("ui.sbarlines", this);
      ClientState.getClientState().addClientStateListener("ui.showsbar", this);
      ClientState.getClientState().addClientStateListener("ui.font", this);
   } 

   public Dimension getPreferredSize()
   {
      if (contents.getTotalLines() == 0)
      {
         return new Dimension(Integer.MAX_VALUE, 1);
      }
      return super.getPreferredSize();
   }

   public void rehashValues()
   {
      if (ClientState.getClientState().isOption("ui.showsbar", true))
      {
         int lines = ClientState.getClientState().getInteger("ui.sbarlines", ClientDefaults.ui_sbarlines);
         contents.setNumberOfLines(lines); 
      }
      else
      {
         contents.setNumberOfLines(0);
      }
   }

   public void propertyChanged(String var, String parms) 
   { 
      if (var.equals("statusbar"))
      {
         repaint();
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               rehashValues(); 
               rehash(); 
               revalidate(); 
               repaint();
            }
         });
      }
   }

   public void rehash()
   {
      if (capabilities == null)
         return;

      event.put("$query", parent.getQuery());
      event.put("$window", parent.getName());

      for (int x = 0; x < contents.getTotalLines(); x++)
      {
         event.put("$line", ""+x);

         String lhs = capabilities.getOutputCapabilities().parseSet(event, "SBAR_LEFT");
         String rhs = capabilities.getOutputCapabilities().parseSet(event, "SBAR_RIGHT");

         if (lhs == null) { lhs = ""; }
         if (rhs == null) { rhs = ""; }

         contents.setLine(lhs, rhs, x);
      }

      lastRehash = System.currentTimeMillis();
   }

   public void paint(Graphics g)
   {
      if ((System.currentTimeMillis() - lastRehash) > 10000)
      {
         rehash();  // force a rehash every 10 seconds regardless...
      }

      super.paint(g);
   }

/*   protected void finalize()
   {
     System.out.println("Finalizing Window Status Bar");
   } */
}
