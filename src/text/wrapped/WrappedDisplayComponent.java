package text.wrapped;

import text.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.font.*;

public class WrappedDisplayComponent extends JComponent implements ChangeListener
{
   protected WrappedData data;

   public WrappedDisplayComponent()
   {
      setOpaque(false);
      setDoubleBuffered(false);
   }

   public void setWrappedData(WrappedData _data)
   {
      data = _data;
   }

   public void stateChanged(ChangeEvent e)
   {
      repaint();
   }

   public void paint (Graphics g)
   {
      TextSource.initGraphics(g); 

      WrappedContainer text = data.getCurrentText();

      data.setExtent((getHeight() - 5) / (TextSource.fontMetrics.getHeight() * 3));

      if (text == null)
      {
         return;
      }

      int checkY = (g.getClipBounds()).y - 10;             // reverse these if
      int checkH = checkY+(g.getClipBounds()).height + 20; // painting fucks up

      int width = super.getWidth();
      int height = super.getHeight();

      g.setFont(TextSource.clientFont);

      int baseline = height - 5; // gives us a 5 pixel buffer
                                 // between the textbox and the textarea

      WrappedContainer head = text;
      AttributedText[] strings;

      if (data.getSelection() != null)
      {
         data.getSelection().clear();
      }

      while (head != null && baseline > 0)
      {
         head.touch(width);
         
         strings = head.getWrappedText();

         for (int x = 0; x < strings.length && baseline > 0; x++)
         {
            if (baseline <= checkH && baseline >= checkY)
            {
               TextSource.drawBackground(g, strings[x], 0, baseline);
               drawSelection(g, strings[x], baseline, x == 0);
               TextSource.drawForeground(g, strings[x], 0, baseline);
            }

            baseline -= (TextSource.fontMetrics.getHeight() + 2);
         }

         head = head.next();
      }
   }

   /** Draws the selected text and interacts with the SelectionSpace object calculating the actual text we're going to put
       into the clipboard.  This is some fugly code.  As far as I can tell it works though. */   
   public void drawSelection(Graphics g, AttributedText text, int baseline, boolean beginning)
   {
      if (data.getSelection() == null)
      {
         return;
      }

      SelectionSpace select = data.getSelection();

      TextSource.setupSelection(g);

      String stext;

      if (select.isOnlyLine(baseline))
      {
         int indexStart = text.getRange(0, select.getSingleStart()).length();
         int indexStop  = text.getRange(0, select.getSingleEnd()).length();

         stext = text.getText();

         int height = TextSource.fontMetrics.getHeight();
         int start  = TextSource.fontMetrics.stringWidth(stext.substring(0, indexStart));
         int stop   = TextSource.fontMetrics.stringWidth(stext.substring(0, indexStop));

         select.append(stext.substring(indexStart, indexStop));

         g.fillRect(start, baseline - height + 2, stop - start, height + 2); // the +2 may become a +4

//         g.setColor(Color.black);
//         g.drawString(stext.substring(indexStart, indexStop), start, baseline);
      }
      else if (select.isStartLine(baseline))
      {
         if (beginning)
         {
            select.touch();
         }

         stext = text.getRange(0, select.getSelectionStart());

         int height = TextSource.fontMetrics.getHeight();
         int width  = TextSource.fontMetrics.stringWidth(stext);

         String temps = text.getText();

         stext = temps.substring(stext.length(), temps.length());
         select.append(stext);

         g.fillRect(width, baseline - height + 2, text.getWidth() - width, height + 2); // the +2 may become a +4

 //        g.setColor(Color.black);
 //        g.drawString(stext, width, baseline);
      }
      else if (select.isEndLine(baseline))
      {
         if (beginning)
         {
            select.touch();
         }

         int height = TextSource.fontMetrics.getHeight();
         int width  = TextSource.fontMetrics.stringWidth(text.getRange(0, select.getSelectionEnd()));

         if (text.isIndent()) 
         {
            stext = text.next.getRange(0, select.getSelectionEnd());
            select.append(stext);
            select.append(" ");
         }
         else
         {
            stext = text.getRange(0, select.getSelectionEnd());
            select.append(stext);
         }

         g.fillRect(0, baseline - height + 2, width, height + 2); // the +2 may become a +4

 //        g.setColor(Color.black);
 //        g.drawString(text.getRange(0, select.getSelectionEnd()), 0, baseline);
      }
      else if (select.isSelectedLine(baseline))
      {
         if (beginning)
         {
            select.touch();
         }

         int height = TextSource.fontMetrics.getHeight();
         g.fillRect(0, baseline - height + 2, text.getWidth(), height + 2); // the +2 may become a +4

   //      g.setColor(Color.black);
    //     g.drawString(text.getText(), 0, baseline);

         if (text.isIndent())
         {
            text = text.next;
            select.append(text.getText());
            select.append(" ");
         }
         else
         {
            select.append(text.getText());
         }

      }
//      g.setColor(Color.red);  // just some debugging to see the clipping for the text area..
  //    ((Graphics2D)g).draw(select.getChangedArea());
   }
}
