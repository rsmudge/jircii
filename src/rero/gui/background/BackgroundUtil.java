package rero.gui.background;

import java.awt.*;
import java.awt.image.*;

import java.io.*;

import javax.swing.*;
import java.util.*;

public class BackgroundUtil
{
   public static final int STYLE_TILE      = 0; // draw tiling from the corner of the window
   public static final int STYLE_CENTER    = 1; // center the image (in the window or relative to the screen, tbd I guess)
   public static final int STYLE_FILL      = 2; // fill the *window* with the image (slow)
   public static final int STYLE_STRETCHED = 3; // fill the *window* with the image (slow)

   public static final int BG_DEFAULT     = 0;
   public static final int BG_SOLID       = 1;
   public static final int BG_TRANSPARENT = 2;
   public static final int BG_IMAGE       = 3;


   public static void drawBackground(Component source, Graphics g, BackgroundProperties bgConfig)
   {
       if (bgConfig.getImage(source) == null)
       {
           drawSafeBackground(source, g, bgConfig); // in case an image isn't selected... we want some kind of background
           return;
       }

       switch(bgConfig.getStyle())
       {
          case STYLE_TILE:
            drawImageTiled(source, g, bgConfig);
            break;
          case STYLE_FILL:
            drawImageFill(source, g, bgConfig);
            break;
          case STYLE_STRETCHED:
            drawImageStretched(source, g, bgConfig);
            break;
          case STYLE_CENTER:
            drawImageCentered(source, g, bgConfig);
            break;
          default:
            drawImageTiled(source, g, bgConfig);
            break;
       }
   }

   public static void drawTintedTransparency(Component source, Graphics g, BackgroundProperties bgProperties)
   {
       if (bgProperties.getTint() > 0)
       {
          int x, y, width, height;

          x      = g.getClipBounds().x;
          y      = g.getClipBounds().y;
          width  = g.getClipBounds().width;
          height = g.getClipBounds().height;

          g.setColor(bgProperties.getColor());

          Graphics2D g2 = (Graphics2D)g;
          Composite oc = g2.getComposite();
          AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bgProperties.getTint());

          g2.setComposite(ac);
          g2.fillRect(x, y, width, height);

          g2.setComposite(oc);
       }
   }

   public static void drawSafeBackground(Component source, Graphics g, BackgroundProperties bgProperties)
   {
       int x, y, width, height;

       x      = g.getClipBounds().x;
       y      = g.getClipBounds().y;
       width  = g.getClipBounds().width;
       height = g.getClipBounds().height;

       g.setColor(bgProperties.getColor());

       g.fillRect(x, y, width, height);
   }

   /** draws the image to fill the specified component, hardware scaling is pretty damned fast but we still cache the scaled image anyways just to be safe */
   public static void drawImageFill(Component source, Graphics g, BackgroundProperties properties)
   {
      Image temp = properties.getTransformedImage(); 

      Dimension compare = null;
      int tx = 0, ty = 0;

      if (properties.isRelative())
      {
         compare = Toolkit.getDefaultToolkit().getScreenSize();
         tx = Math.abs(source.getLocationOnScreen().x) % Toolkit.getDefaultToolkit().getScreenSize().width;
         ty = Math.abs(source.getLocationOnScreen().y) % Toolkit.getDefaultToolkit().getScreenSize().height; 

         // the reason for all the weird Math.abs() and %'ing is to make the relative background stuff work with 
         // dual (or more *shrug*) display setups.  
      }
      else
      {
         compare = source.getSize();
      }

      if (temp == null || temp.getWidth(null) != (int)compare.getWidth() || temp.getHeight(null) != (int)compare.getHeight())
      {
         Image imageData = properties.getImage(source);

         Image cached = source.createImage((int)compare.getWidth(), (int)compare.getHeight());

         Graphics2D g2 = (Graphics2D)cached.getGraphics();
         g2.drawImage(imageData, 0, 0, (int)compare.getWidth(), (int)compare.getHeight(), null);
         g2.dispose();

         properties.setTransformedImage(cached);

         g.drawImage(cached, 0 - tx, 0 - ty, null);
         return;
      }

      g.drawImage(temp, 0 - tx, 0 - ty, null);
   }

   public static void drawImageCentered(Component source, Graphics g, BackgroundProperties properties)
   {
      Image temp = properties.getTransformedImage(); 

      Dimension compare = null;
      int tx = 0, ty = 0;

      if (properties.isRelative())
      {
         compare = Toolkit.getDefaultToolkit().getScreenSize();
         tx = Math.abs(source.getLocationOnScreen().x) % Toolkit.getDefaultToolkit().getScreenSize().width;
         ty = Math.abs(source.getLocationOnScreen().y) % Toolkit.getDefaultToolkit().getScreenSize().height; 
      }
      else
      {
         compare = source.getSize();
      }

      if (temp == null || temp.getWidth(null) != (int)compare.getWidth() || temp.getHeight(null) != (int)compare.getHeight())
      {
         Image imageData = properties.getImage(source);

         Image cached = source.createImage((int)compare.getWidth(), (int)compare.getHeight());

         int x_offset = ((int)compare.getWidth()  - imageData.getWidth(null)) / 2;
         int y_offset = ((int)compare.getHeight() - imageData.getHeight(null)) / 2;

         Graphics2D g2 = (Graphics2D)cached.getGraphics();

         g2.setColor(properties.getColor());
         g2.fillRect(0, 0, (int)compare.getWidth(), (int)compare.getHeight());

         g2.drawImage(imageData, x_offset, y_offset, imageData.getWidth(null), imageData.getHeight(null), null);

         g2.dispose();

         properties.setTransformedImage(cached);

         g.drawImage(cached, 0 - tx, 0 - ty, null);
         return;
      }

      g.drawImage(temp, 0 - tx, 0 - ty, null);
   }

   public static void drawImageStretched(Component source, Graphics g, BackgroundProperties properties)
   {
      Image temp = properties.getTransformedImage(); 

      Dimension compare = null;
      int tx = 0, ty = 0;

      if (properties.isRelative())
      {
         compare = Toolkit.getDefaultToolkit().getScreenSize();
         tx = Math.abs(source.getLocationOnScreen().x) % Toolkit.getDefaultToolkit().getScreenSize().width;
         ty = Math.abs(source.getLocationOnScreen().y) % Toolkit.getDefaultToolkit().getScreenSize().height; 
      }
      else
      {
         compare = source.getSize();
      }

      if (temp == null || temp.getWidth(null) != (int)compare.getWidth() || temp.getHeight(null) != (int)compare.getHeight())
      {
         Image imageData = properties.getImage(source);

         Image cached = source.createImage((int)compare.getWidth(), (int)compare.getHeight());

         // calculate appropriate height and offset

         float ratio;
   
         float x_diff = ((float)compare.getWidth() / (float)imageData.getWidth(null));
         float y_diff = ((float)compare.getHeight() / (float)imageData.getHeight(null));
       
         // use the largest ratio to get the image to take up the whole area
         if (x_diff > y_diff)
         {
            ratio = x_diff;
         }
         else
         {
            ratio = y_diff;
         }

         int width  = (int)(imageData.getWidth(null) * ratio);
         int height = (int)(imageData.getHeight(null) * ratio); 

         int x_offset = ((int)compare.getWidth() - width) / 2;
         int y_offset = ((int)compare.getHeight() - height) / 2;

         Graphics2D g2 = (Graphics2D)cached.getGraphics();

         g2.setColor(properties.getColor());
         g2.fillRect(0, 0, (int)compare.getWidth(), (int)compare.getHeight());

         g2.drawImage(imageData, x_offset, y_offset, width, height, null);

         g2.dispose();

         properties.setTransformedImage(cached);

         g.drawImage(cached, 0 - tx, 0 - ty, null);
         return;
      }

      g.drawImage(temp, 0 - tx, 0 - ty, null);
   }


   public static void drawImageTiled(Component source, Graphics g, BackgroundProperties properties)
   {
      Image imageData = properties.getImage(source);

      int checkY = g.getClipBounds().y;
      int checkH = g.getClipBounds().height+checkY;
      checkY -= imageData.getHeight(null);

      int checkX = g.getClipBounds().x;
      int checkL = g.getClipBounds().width+checkX;
      checkX -= imageData.getWidth(null);

      int tx = 0;
      int ty = 0;

      if (properties.isRelative())
      {
         tx = Math.abs(source.getLocationOnScreen().x) % Toolkit.getDefaultToolkit().getScreenSize().width;
         ty = Math.abs(source.getLocationOnScreen().y) % Toolkit.getDefaultToolkit().getScreenSize().height; 
      }

      int x = source.getWidth();
      int y = source.getHeight();

      int x1 = 0-tx;
      int y1 = 0-ty;
      while (x1 < x)
      {
         while (y1 < y)
         {
            if ((x1 >= checkX) && (x1 <= checkL) && (y1 >= checkY) && (y1 <= checkH))
            {
               g.drawImage(imageData, x1, y1, null);
            }
            y1 += imageData.getHeight(null);
         }
         x1 += imageData.getWidth(null);
         y1 = 0-ty;
      }
   }

   /** gets a managed image with the specified alpha tinting and all that jazz */
   public static Image getManagedImage(Component source, String file, float tint, Color solid)
   {
      if (!(new File(file)).exists())
      {
         return null;
      }

      Image imageData = new javax.swing.ImageIcon(file).getImage();

      if (tint > 0)
      {
         Image imageDataTinted = source.createImage(imageData.getWidth(null), imageData.getHeight(null));

         Graphics2D g2 = (Graphics2D)imageDataTinted.getGraphics();
         Composite oc = g2.getComposite();

         g2.drawImage(imageData, 0, 0, null);

         AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tint);
         g2.setComposite(ac);
         g2.setColor(solid);
         g2.fillRect(0, 0, imageDataTinted.getWidth(null), imageDataTinted.getHeight(null));

         g2.dispose();

         return imageDataTinted;
      }

      return imageData;
   }
}

