package rero.gui.background;

import javax.swing.*;
import java.awt.*;

import rero.config.*;

import rero.gui.background.*;
import java.io.*;

public class BackgroundPanel extends JPanel implements ClientStateListener
{
   protected static BackgroundProperties bgProperties;

   public BackgroundPanel()
   {
      if (bgProperties == null)
      {
         bgProperties = new BackgroundProperties("window", Color.black, BackgroundUtil.BG_SOLID);
      }

      ClientState.getClientState().addClientStateListener("window", this);  // forces a repaint when the window gets changed
   }

   public void propertyChanged(String property, String parms)
   {
      repaint();
   }

   public void paint(Graphics g)
   {
      int x, y, width, height;

      x      = g.getClipBounds().x;
      y      = g.getClipBounds().y;
      width  = g.getClipBounds().width;
      height = g.getClipBounds().height;

      switch (bgProperties.getType())
      {
         case BackgroundUtil.BG_DEFAULT:
         case BackgroundUtil.BG_SOLID:
           g.setColor(bgProperties.getColor());
           g.fillRect(x, y, width, height);          
           paintChildren(g);
           break;
         case BackgroundUtil.BG_IMAGE:
           BackgroundUtil.drawBackground(this, g, bgProperties);
           paintChildren(g);
           break;
      }
   }
}
