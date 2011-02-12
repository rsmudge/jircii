package rero.gui.background;

import javax.swing.*;
import java.awt.*;

import rero.config.*;

import rero.gui.background.*;
import java.io.*;

public class BackgroundToolBar extends JToolBar implements ClientStateListener
{
   protected static BackgroundProperties bgProperties;

   public BackgroundToolBar()
   {
      if (bgProperties == null)
      {
         bgProperties = new BackgroundProperties("statusbar", BackgroundUtil.BG_DEFAULT);
      }

      ClientState.getClientState().addClientStateListener("statusbar", this);
   }

   public boolean isOpaque()
   {
      return (bgProperties.getType() == BackgroundUtil.BG_DEFAULT);
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
           super.paint(g);
           paintBorder(g);
           break;
         case BackgroundUtil.BG_SOLID:
           g.setColor(bgProperties.getColor());
           g.fillRect(x, y, width, height);          
           paintChildren(g);
           break;
         case BackgroundUtil.BG_TRANSPARENT:
           BackgroundUtil.drawTintedTransparency(this, g, bgProperties);
           paintChildren(g);
           break;
         case BackgroundUtil.BG_IMAGE:
           BackgroundUtil.drawBackground(this, g, bgProperties);
           paintChildren(g);
           break;
      }
   }
}
