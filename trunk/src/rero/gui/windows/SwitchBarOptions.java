package rero.gui.windows;

import java.awt.*;
import javax.swing.*;

import java.util.*;

import rero.config.*;

public class SwitchBarOptions implements ClientStateListener
{
   protected JComponent container;
   protected JComponent switchbar;
   protected JComponent panel;

   protected static ColorListener color = null;

   public SwitchBarOptions(JComponent c, JComponent s)
   {
      container = c;
      switchbar = s;

      ClientState.getClientState().addClientStateListener("switchbar.position", this);
      ClientState.getClientState().addClientStateListener("switchbar.enabled", this);

      rehash();

      if (color == null)
      {
         color = new ColorListener();
      }
   }

   public static Color getHighlightColor()
   {
      return color.getColor();
   }

   public static boolean isHilightOn()
   {
      return color.isHilightOn();
   }

   public void rehash()
   {
      container.remove(switchbar);
      boolean enabled  = ClientState.getClientState().isOption("switchbar.enabled", true);
      int     position = ClientState.getClientState().getInteger("switchbar.position", 0);

      if (enabled)
      {
         switch (position)
         {
            case 0:
               container.add(switchbar, BorderLayout.NORTH);
               break;
            case 1:
               container.add(switchbar, BorderLayout.SOUTH);
               break;
            case 2:
               container.add(switchbar, BorderLayout.WEST);
               break;
            case 3:
               container.add(switchbar, BorderLayout.EAST);
               break;
         }
      }
   }

   public void propertyChanged(String key, String value)
   {
      rehash();
      container.revalidate();
   }

   private static class ColorListener implements ClientStateListener
   {
      protected Color   theColor;
      protected boolean hilight;
  
      public ColorListener()
      {
         ClientState.getClientState().addClientStateListener("switchbar.color", this);
         ClientState.getClientState().addClientStateListener("switchbar.hilight", this);

         propertyChanged(null, null);
      }

      public void propertyChanged(String key, String value)
      {
         theColor = ClientState.getClientState().getColor("switchbar.color", ClientDefaults.switchbar_color);
         hilight  = ClientState.getClientState().isOption("switchbar.hilight", ClientDefaults.switchbar_hilight);
      }

      public boolean isHilightOn()
      {
         return hilight;
       }

      public Color getColor() 
      {
         return theColor;
      }      
   }
}

