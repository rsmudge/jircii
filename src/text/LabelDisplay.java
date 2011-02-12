package text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import rero.config.*;

public class LabelDisplay extends JComponent implements MouseListener
{
   protected int lines;
   protected AttributedString[] left;

   protected AttributedString[] right;
   protected int[]              right_widths; // store the widths of the rightmost strings

   public void setNumberOfLines(int l)
   {
      lines = l;
      left = new AttributedString[l];
      right = new AttributedString[l];
      right_widths = new int[l];

      revalidate();
   }

   public int getTotalLines()
   {
      return lines;
   }

   public Dimension getPreferredSize()
   {
      int potheight = (lines * TextSource.fontMetrics.getHeight()) + 2;
     
      return new Dimension(Integer.MAX_VALUE, potheight);
   }

   public void setLine(String left_text, String right_text, int lineno)
   {
      lineno = left.length - (lineno + 1);

      left[lineno]         = AttributedString.CreateAttributedString(left_text);
      right[lineno]        = AttributedString.CreateAttributedString(right_text);
 
      left[lineno].assignWidths();
      right[lineno].assignWidths();

      right_widths[lineno] = right[lineno].getAttributedText().getWidth();
   }

   public LabelDisplay()
   {
      setNumberOfLines(1);
      addMouseListener(this);
   }

   public void paint (Graphics g)
   {
      TextSource.initGraphics(g);

      int checkY = (g.getClipBounds()).y - 9;             // reverse these if
      int checkH = checkY+(g.getClipBounds()).height + 20; // painting fucks up

      int width = super.getWidth();
      int height = super.getHeight();

      int baseline = height - TextSource.fontMetrics.getDescent() - 1; // gives us a 5 pixel buffer       was 5
                                                                         // between the textbox and the textarea

      //g.drawLine(0, baseline, 1024, baseline);


      for (int x = 0; x < left.length && baseline > 0 && left[x] != null; x++)
      {
         if (baseline <= checkH && baseline >= checkY)
         {
            TextSource.drawText(g, left[x].getAttributedText(), 0, baseline);
         }

         baseline -= (TextSource.fontMetrics.getHeight());
      }

      baseline = height - TextSource.fontMetrics.getDescent() - 1; // gives us a 5 pixel buffer       was 5
                                                                         // between the textbox and the textarea
      for (int x = 0; x < right.length && baseline > 0 && right[x] != null; x++)
      {
         if (baseline <= checkH && baseline >= checkY)
         {
            TextSource.drawText(g, right[x].getAttributedText(), width - right_widths[x], baseline);
         }

         baseline -= (TextSource.fontMetrics.getHeight());
      }
   }

   public int translateToLineNumber(int pixely)
   {
      int baseline = getHeight() - TextSource.fontMetrics.getDescent() - 1; // gives us a 5 pixel buffer       was 5
                                                                         // between the textbox and the textarea

      for (int x = 0; x < left.length && baseline > 0 && left[x] != null; x++)
      {
         if (pixely >= (baseline - TextSource.fontMetrics.getHeight() - 0))
         {
            return x;
         }

         baseline -= (TextSource.fontMetrics.getHeight() + 0);
      }

      return 0;
   }

   public void mousePressed(MouseEvent ev) { }
   public void mouseEntered(MouseEvent ev) { }
   public void mouseExited(MouseEvent ev) { }
   public void mouseReleased(MouseEvent ev) { }

   public void mouseClicked(MouseEvent ev)
   {
      if (ev.isShiftDown())
      {
         int lineno = translateToLineNumber(ev.getY());

         int width;
         AttributedText iter;

         if (ev.getX() >= (getWidth() - right_widths[lineno]))
         {
            width = getWidth() - right_widths[lineno]; 
            iter  = right[lineno].getAttributedText();  
         }
         else
         {
            width = 0;
            iter  = left[lineno].getAttributedText();  
         }

         while (iter != null && (iter.width + width) < ev.getX())
         {
            width = width + iter.width;
            iter = iter.next;
         }

         if (iter != null)
         {
            int            index;

            if (ev.isControlDown() && iter.backIndex > -1)
            {
               index = iter.backIndex;
            }
            else
            {
               index = iter.foreIndex;
            }

            ModifyColorMapDialog.showModifyColorMapDialog(this, index);
         }
         return;
      }
   }

   
}
