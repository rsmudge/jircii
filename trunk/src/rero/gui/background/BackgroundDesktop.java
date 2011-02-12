package rero.gui.background;

import javax.swing.*;
import java.awt.*;

import rero.config.*;

import rero.gui.background.*;
import java.io.*;

public class BackgroundDesktop extends JPanel implements ClientStateListener
{
   protected BackgroundProperties bgProperties;
//   protected JDesktopPane         desktop;
   protected JComponent           desktop;

   public BackgroundDesktop(JComponent pane)
   {
      desktop = pane;

      if (bgProperties == null)
      {
         bgProperties = new BackgroundProperties("desktop", BackgroundUtil.BG_DEFAULT);
      }

      ClientState.getClientState().addClientStateListener("desktop", this);
   }

   public Dimension getSize()
   {
      return desktop.getSize();
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
           break;
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
