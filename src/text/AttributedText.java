package text;

import java.awt.Color;

public class AttributedText
{
   public static int INSTANCES = 0;

   public int     foreIndex =  0;
   public int     backIndex = -1;

   public boolean isUnderline  = false;
   public boolean isBold       = false;
   public boolean isReverse    = false;

   public int     start        = 0;
   public int     end          = 0; 

   public int     width;  // we're going to cache the width of the string...  string width is an expensive operation.

   public AttributedText next;
   public String  text;

   public AttributedText()
   {
      INSTANCES++;
   } 

   public void finalize()
   {
      INSTANCES--;
   }

   public AttributedText copyAttributes()
   {
      AttributedText temp = new AttributedText();
      temp.foreIndex   = foreIndex;
      temp.backIndex   = backIndex;
      temp.isUnderline = isUnderline;
      temp.isBold      = isBold;
      temp.isReverse   = isReverse;

      return temp;
   }

   public AttributedText cloneAttributedText()
   {
      AttributedText temp = copyAttributes();
      temp.width          = width;
      temp.text           = text;

      return temp;
   }

   public AttributedText cloneList()
   {
      AttributedText head = cloneAttributedText();
      AttributedText tail = head;
      AttributedText temp = this.next;

      while (temp != null)
      {
         tail.next       = temp.cloneAttributedText();

         tail = tail.next;
         temp = temp.next;
      }

      return head;
   }

   public void setIndent()
   {
      start = Integer.MAX_VALUE;
      end   = Integer.MIN_VALUE;
   }

   public boolean isIndent()
   {
      return (start == Integer.MAX_VALUE && end == Integer.MIN_VALUE);
   }

   public int getWidth()
   {
      int width = 0;

      AttributedText temp = this;
      while (temp != null)
      {
         width += temp.width;
         temp = temp.next;
      }

      return width;
   }

   public AttributedText getAttributesAt(int location)
   {
      if (location < width)
      {
         return this;
      }

      if (next != null)
      {
         return next.getAttributesAt(location - width);
      }

      return null;
   }

   public String getText()
   {
      StringBuffer rv = new StringBuffer();

      AttributedText temp = this;
      while (temp != null)
      {
         rv.append(temp.text);
         temp = temp.next;
      }

      return rv.toString();
   }

   private static int findIndex(String text, int max_size, int start, int width)
   {
      int x = start + 1;
      int twidth = 0;

      while ((x + x) < text.length() && twidth < max_size)
      {
         x += x;
         twidth = TextSource.fontMetrics.stringWidth(text.substring(0, x)) + width;
      }

      x      = x / 2;
      twidth = 0;

      while (x < text.length() && twidth < max_size)
      {
         twidth = TextSource.fontMetrics.stringWidth(text.substring(0, x)) + width;
         x++;
      }

      return x - 1;
   }
 
   public String getRange(int x_start, int x_end)
   {
      AttributedText temp = this;
      StringBuffer rv = new StringBuffer();

      int start, end;
      int width = 0;

      while (temp != null)
      {
         start = width;
         end   = width + temp.width;

         if (x_start > start && x_end <= end)
         {
            // special case, string range is just one type of attribute
            int x = findIndex(temp.text, x_start, 0, width);
            int y = findIndex(temp.text, x_end,   x, width);

            return temp.text.substring(x, y);
         }
         else if (x_start <= start && x_end >= end)
         {
            // get the whole string...
            rv.append(temp.text);
         }
         else if (x_start <= start && (x_end <= end && x_end > x_start))
         {
            // get the substring from 0 .. the end
            int x = findIndex(temp.text, x_end, 0, width);

            if (x > 1)
            {
               rv.append(temp.text.substring(0, x));
            }
         }
         else if (x_start > start && x_start <= end && x_end >= end)
         {
            // get the substring from n .. the end
            int x = findIndex(temp.text, x_end, 0, width);

            rv.append(temp.text.substring(x, temp.text.length()));
         }

         width += temp.width;
         temp = temp.next;
      }

      return rv.toString();
   }

   public String toString()
   {
      String attribs = "";
      if (isBold) { attribs = attribs + "B"; }
      if (isUnderline) { attribs = attribs + "U"; }
      if (isReverse) { attribs = attribs + "R"; }

      String addon = "|";

      if (next != null)
      {
         addon = next.toString();
      }

      if (start == 0 && end == 0)
      {
          return "[\"" + text + "\"," + width + "px:" + attribs + ":" + foreIndex + "," + backIndex + "]->" + addon;
      }
    
      return "[\"" + text + "\"," + start + "-" + end + ":" + attribs + ":" + foreIndex + "," + backIndex + "]->" + addon;
   }
}
