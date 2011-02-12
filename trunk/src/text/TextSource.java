package text;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import rero.util.*;

import rero.config.*;
import java.io.*;
import rero.gui.*;

import java.awt.font.*;

public class TextSource
{
   /** font used by all jIRC windows...  I'm not doing per-window fonts. */ 
   public   static Font        clientFont;         
   public   static FontMetrics fontMetrics;
   private  static Object      antiAliasHint;

   public static final int UNIVERSAL_TWEAK = 2; // number of pixels between drawn text and whatnot...

   protected static TextSourceListener listener; // a reference has to be mantained in order for the listener to not die

   public static Color colorTable[];

   public static void saveColorMap()
   {
       try
       {
          FileOutputStream ostream = new FileOutputStream(new File(ClientState.getBaseDirectory() ,"color.map"));
          ObjectOutputStream o = new ObjectOutputStream(ostream);
          o.writeObject(colorTable);
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       ClientState.getClientState().fireChange("color.map", null);
   }

   static 
   {
       try
       {
          ObjectInputStream p = new ObjectInputStream(ClientState.getClientState().getResourceAsStream("color.map"));
          colorTable = (Color[])p.readObject();
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }

       if (colorTable == null)
       {
          colorTable = new Color[100]; 
          colorTable[0] = Color.lightGray;
          colorTable[1] = new Color(0, 0, 0);
          colorTable[2] = new Color(0, 0, 128);
          colorTable[3] = new Color(0, 144, 0);
          colorTable[4] = new Color(255, 0, 0);
          colorTable[5] = new Color(128, 0, 0);
          colorTable[6] = new Color(160, 0, 160);
          colorTable[7] = new Color(255, 128, 0);
          colorTable[8] = new Color(255, 255, 0);
          colorTable[9] = new Color(0, 255, 0);
          colorTable[10] = new Color(0, 144, 144);
          colorTable[11] = new Color(0, 255, 255);
          colorTable[12] = new Color(0, 0, 255);
          colorTable[13] = new Color(255, 0, 255);
          colorTable[14] = new Color(128, 128, 128);
          colorTable[15] = Color.lightGray;
          colorTable[16] = new Color(255, 255, 255);

          for (int x = 17; x < colorTable.length; x++)
          {
             colorTable[x] = colorTable[0];
          }

          saveColorMap();
       }

       listener = new TextSourceListener(); // takes care of itself..
   }

   private static final Color selectionColor = new Color(40, 105, 125);

   public static void setupSelection(Graphics g) 
   {
      Color selectc = UIManager.getColor("TextField.selectionBackground");
      g.setColor(selectc != null ? selectc : selectionColor);
   }

   public static void initGraphics(Graphics g)
   {
      g.setFont(clientFont);
      
      Graphics2D g2 = (Graphics2D)g;
//      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliasHint);
   }

   public static int translateToLineNumber(int pixely)
   {
      return (pixely - 5) / (TextSource.fontMetrics.getHeight() + 2);
   }

   public static void drawForeground(Graphics g, AttributedText text, int x_component, int baseline)
   {
      int height      = TextSource.fontMetrics.getHeight();

      while (text != null)
      {
         g.setColor(TextSource.colorTable[text.foreIndex]);

         if (text.isBold)
         {
            g.setColor(g.getColor().brighter());
         }

         if (text.isReverse)
         {
            g.setColor(g.getColor().darker());
         }

         if (text.isUnderline)
         {
            g.drawLine(x_component, baseline + 1, x_component + text.width, baseline + 1);
         }

         g.drawString(text.text, x_component, baseline);

         x_component += text.width;
         text = text.next;
      }
   }
 
   public static void drawBackground(Graphics g, AttributedText text, int x_component, int baseline)
   {
      int height      = TextSource.fontMetrics.getHeight();

      while (text != null)
      {
         if (text.backIndex != -1)
         {
            g.setColor(TextSource.colorTable[text.backIndex]);
            g.fillRect(x_component, baseline - height + 2, text.width, height + 2); // the +2 may become a +4
         }

         x_component += text.width;
         text = text.next;
      }
   }

   public static void drawText(Graphics g, AttributedText text, int x_component, int baseline)
   {
      drawBackground(g, text, x_component, baseline);
      drawForeground(g, text, x_component, baseline);
   }

   protected static class TextSourceListener implements ClientStateListener
   {
      public TextSourceListener()
      {
          ClientState.getClientState().addClientStateListener("ui.font", this);
          ClientState.getClientState().addClientStateListener("ui.antialias", this);
          rehashValue();
      }

      public void rehashValue()
      {
          if (ClientState.getClientState().isOption("ui.antialias", ClientDefaults.ui_antialias))
          {
             antiAliasHint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
          }
          else
          {
             antiAliasHint = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
          }

          clientFont  = ClientState.getClientState().getFont("ui.font", ClientDefaults.ui_font);
          fontMetrics = new AdjustedFontMetrics(clientFont, Toolkit.getDefaultToolkit().getFontMetrics(clientFont));
      }

      public void propertyChanged(String value, String parms)
      {
          rehashValue();
          SessionManager.getGlobalCapabilities().frame.validate();
          SessionManager.getGlobalCapabilities().frame.repaint();
      }
   }

   private static class AdjustedFontMetrics extends FontMetrics
   {
      private int               adj_ascent, adj_descent, height;
      private FontMetrics       metrics;
      private FontRenderContext context;
      private Font              font;

      public AdjustedFontMetrics(Font wayneFonts, FontMetrics _metrics)
      {
         super(wayneFonts);

         metrics = _metrics;

         adj_ascent  = Math.abs(metrics.getMaxAscent()); // ascents shouldn't have negative numbers, screws up the painting
         adj_descent = Math.abs(metrics.getMaxDescent()); // descents shouldn't have negative numbers, screws up the painting

         context     = new FontRenderContext(null, antiAliasHint == RenderingHints.VALUE_TEXT_ANTIALIAS_ON, false);
         font        = wayneFonts;

         height      = adj_ascent + adj_descent;
      }

      public int getAscent()
      {
         return adj_ascent;
      }

      public int getDescent()
      {
         return adj_descent;
      }

      public int getHeight()
      {
         return height;
      }

      public int stringWidth(String str)
      {
         return (int)Math.round(font.getStringBounds(str, context).getWidth());
      }
   } 
}
