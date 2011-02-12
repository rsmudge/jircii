package text.list;

import text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.font.*;

import rero.config.*;

public class ListDisplayComponent extends JComponent implements ChangeListener, ClientStateListener
{
   protected ListData data;

   public ListDisplayComponent()
   {
      setOpaque(false);
      setDoubleBuffered(false);

      ClientState.getClientState().addClientStateListener("listbox.width", this);

      propertyChanged(null, null);
   }

   public void installDataSource(ListData _data)
   {
       data = _data;
   }

   protected String test_string   = "";
   private static final String widthstring = "1234567890123456789012345678901234567890123456789012345678901234567890";
     
   public void propertyChanged(String a, String b) 
   { 
      test_string   = widthstring.substring(0, ClientState.getClientState().getInteger("listbox.width", ClientDefaults.listbox_width));
      revalidate();
   } 

   public void stateChanged(ChangeEvent e)
   {
       repaint();
   }

   public Dimension getPreferredSize()
   {
       FontMetrics fm = TextSource.fontMetrics;
       return new Dimension(fm.stringWidth(test_string) + TextSource.UNIVERSAL_TWEAK, 0);
   }

   private static final Color backupSelection = new Color(45, 105, 125);

   public void paint (Graphics g)
   {
      TextSource.initGraphics(g); 

      data.setExtent(getHeight() / (TextSource.fontMetrics.getHeight() + 2));

      int checkY = (g.getClipBounds()).y - 10;             // reverse these if
      int checkH = checkY+(g.getClipBounds()).height + 20; // painting fucks up

      int width = super.getWidth();
      int height = super.getHeight();

      g.setFont(TextSource.clientFont);

      int baseline = TextSource.fontMetrics.getHeight() - 2; // starting at the top for this one...

      synchronized (data.getSynchronizationKeyOuter())
      {
         synchronized (data.getSynchronizationKeyInner())
         {
            ListElement head = data.head(); // iteration is built into the ListData class, not my favorite pattern 

            while (head != null && baseline < height)
            {
               if (baseline <= checkH && baseline >= checkY)
               {
                  if (head.isSelected())
                  {
                     TextSource.setupSelection(g);

                     int txtheight = TextSource.fontMetrics.getHeight();
                     g.fillRect(0, baseline - txtheight + 2, width, txtheight + 2); // the +2 may become a +4
                  }
                  TextSource.drawText(g, head.getAttributedText(), TextSource.UNIVERSAL_TWEAK, baseline);
               }

               baseline += (TextSource.fontMetrics.getHeight() + 2);

               head = data.next();
            } // end while loop... I do need to reformat this...
         } // end key inner
      } // end key outer
   }
}
