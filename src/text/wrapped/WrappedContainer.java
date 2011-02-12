package text.wrapped;

import text.*;

import java.util.*;
import java.awt.*;
import rero.util.*;

public class WrappedContainer
{
   protected int maxSize;  // max size window can grow to before I force a wordwrap.
   protected int minSize;  // minimum size window can grow to before I force a wrap.

   protected WrappedContainer next = null;
   protected WrappedContainer prev = null;

   protected AttributedString text;

   protected AttributedText[] wrapped; // wrapped text.

   public WrappedContainer(String input) 
   {
      text = AttributedString.CreateAttributedString(input);
   }

   public WrappedContainer next()
   {
      return next;
   }

   public WrappedContainer previous()
   {
      return prev;
   }

   public boolean hasNext()
   {
      return next != null;
   }

   public boolean hasPrevious()
   {
      return prev != null;
   }

   public void setNext(WrappedContainer n)
   {
      next = n;
   }

   public void setPrevious(WrappedContainer p)
   {
      prev = p;
   }

   /** parameter size is the size of the component we're going to be drawing on **/
   public void touch(int size) 
   {
      if (size >= minSize && size <= maxSize)
      {
//         System.out.println(size + " >= " + minSize + " && "+size+" <= " + maxSize + " :" + text.getText());
         return;
      } 

      //
      // wrap the text...
      //
      wrapped = wrap(text, size);

      determineBounds(size);      
   }

   public void reset()
   {
      minSize = 1024 * 768;
      maxSize = 0;
   }

   protected void determineBounds(int size)
   {
      //
      // determine upper and lower bounds for further wrapping.  I don't know how much overhead these
      // functions will be creating so if its a problem the upper and lower bound optimization may turn
      // into a heuristic similiar to how I did it before...
      //
      minSize = 0;
      maxSize = Integer.MAX_VALUE;

      AttributedText temp;

      for (int x = 0; x < wrapped.length; x++)
      {
          int width = 0;

          temp = wrapped[x];
          while (temp != null)
          {
             width += temp.width;
             temp = temp.next;
          }         

          if (width > minSize)
          { 
    //         System.out.println("Lower bound tolerance is " + width + " for " + wrapped[x]);
             minSize = width; 
          }
      }
      maxSize = size + 50;
   }

   /** returns an array of wrapped text, just a note wrapped text is stored in reverse order since drawing happens from the 
       bottom of the component on up.   */
   public AttributedText[] getWrappedText()
   {
       return wrapped;
   }

   /** reconstruct string from attributed text, used by on click mechanism type of deal */
   public String getText()
   {
      return text.getText();
   }

   /** used by url clicking mechanism...   jEAH. */
   public String getTokenAt(AttributedText lineno, int pixelx)
   {
      if (lineno.isIndent())
      {
         pixelx -= lineno.width;
         lineno = lineno.next;
      }
      String temp = lineno.getText();
      TokenizedString tokens = new TokenizedString(temp, " ");

      for (int x = 0; x < (tokens.getTotalTokens() + 1); x++)
      {
         if (TextSource.fontMetrics.stringWidth(tokens.getTokenTo(x)) > pixelx)
         {
            String rv;
 
            if (x > 0) { rv = tokens.getToken(x - 1); }
            else { rv = tokens.getToken(0); }

            //
            // is this token self contained in the total string?
            //
            if (text.getTokens().isToken(rv))
            {
               return rv;  // we know that this is a whole complete token.
            }              
 
            //
            // is this token the beginning of another longer token (in which case we want the whole thing)
            //
            for (int z = 0; z < text.getTokens().getTotalTokens(); z++)
            {
               if (rv.length() < text.getTokens().getToken(z).length())
               {
                   if (text.getTokens().getToken(z).substring(0, rv.length()).equals(rv))
                   {
                      return text.getTokens().getToken(z);
                   }
               }
            }
           
            //
            // is this token contained within some longer token (again in which case we want the whole thing)
            //
            for (int z = 0; z < text.getTokens().getTotalTokens(); z++)
            {
               if (rv.length() < text.getTokens().getToken(z).length())
               {
                   if (text.getTokens().getToken(z).indexOf(rv) > -1)
                   {
                      return text.getTokens().getToken(z);
                   }
               }
            }

            //
            // I give up...
            //
            return rv;
         }
      }

      return null;
   }

   /** used by shift+click mechanism */
   public AttributedText getAttributedTextAt(AttributedText line, int pixelx)
   {
       int width = 0;
       while (line != null && (width + line.width) < pixelx)
       {
          width += line.width;
          line = line.next;
       }

       return line;
   }

   public static AttributedText[] wrap(AttributedString textData, int maxWidth)
   {
      if (maxWidth < 100) { return new AttributedText[0]; } 

      TokenizedString tokens  = textData.getTokens();
 
      LinkedList data = new LinkedList();
      AttributedText head;

      String text    = textData.getText();
      String current = "";    // the current string we are working with and potentially wordwrapping.    

      int tokenNo   = 0; // identifier for our current lineno.
      int start     = 0; // index into the "text" of the starting char for this line.
      int oldlength = 0;

      int indentlen = 0;
      String indent = "";  // indenting stuff, to be taken into account.

      int totalWidth = 0;
 
      while (tokenNo <= tokens.getTotalTokens())
      {
         if (totalWidth >= maxWidth) 
         {
            // the current token has put us over the top, doh!
            int size = TextSource.fontMetrics.stringWidth(tokens.getToken(tokenNo - 1)) + indentlen;

            if (start == oldlength && size < maxWidth)
            {
               //
               // we're stuck in the current token, need to jiggle things a little bit.
               //

               oldlength = current.length();
               tokenNo++;

               current    = tokens.getTokenTo(tokenNo);
               totalWidth = TextSource.fontMetrics.stringWidth(text.substring(start, current.length())) + indentlen;
            }
            else if (size >= maxWidth)
            {
               //
               // the current token is larger than a line of text as it is.  So we're in
               // trouble.
               //

               int y = start + 1;
               while (y < current.length() && (TextSource.fontMetrics.stringWidth(text.substring(start, y)) + indentlen) < maxWidth)
               {
                  y++;
               }
               y--;

               // WRAP
               if (indent.length() > 0)
               {
                   head = new AttributedText();
                   head.text = indent;
                   head.width = indentlen;
                   head.next = textData.substring(start, y);
                   head.setIndent();
               }
               else
               {
                   head = textData.substring(start, y);
               }
               data.add(head);

               // RESET
               indent = "   ";
               indentlen = TextSource.fontMetrics.stringWidth("   ");

               start = y;

               while (start < text.length() && text.charAt(start) == ' ')
               {
                  start++;
               }

               oldlength = start;

               totalWidth = TextSource.fontMetrics.stringWidth(text.substring(start, current.length())) + indentlen;
            }
            else
            {
               //
               // plain jane wrapping case, revert to the last good text position that didn't put us over the top
               // and add it to the data structure for the return value (the data linked list).
               //

                // WRAP
                if (indent.length() > 0)
                {
                   head = new AttributedText();
                   head.text = indent;
                   head.width = indentlen;
                   head.next = textData.substring(start, oldlength);
                   head.setIndent();
                }
                else
                {
                   head = textData.substring(start, oldlength);
                }
                data.add(head); 

                // RESET
                indent = "   ";
                indentlen = TextSource.fontMetrics.stringWidth("   ");

                start = oldlength;

                while (start < text.length() && text.charAt(start) == ' ')
                {
                   start++;
                }

                oldlength = start;
            }
         }
         else
         {
             //
             // advance to the next token...
             //

             oldlength = current.length();
             tokenNo++;

             current    = tokens.getTokenTo(tokenNo);
             totalWidth = TextSource.fontMetrics.stringWidth(text.substring(start, current.length())) + indentlen;
         }
      }
  
      //
      // pick up any left over slop and add it to the data structure...
      //
      if (start < text.length())
      {
          if (indent.length() > 0)
          {
              head = new AttributedText();
              head.text = indent;
              head.width = indentlen;
              head.next = textData.substring(start, oldlength);
              head.setIndent();
          }
          else
          {
              head = textData.substring(start, oldlength);
          }
          data.add(head);
      }
      //
      // convert the linked list to an array.
      //
      AttributedText returnValue[] = new AttributedText[data.size()];
      int x = data.size() - 1;
      ListIterator i = data.listIterator();
      while (i.hasNext())
      {
         returnValue[x] = (AttributedText)i.next();
         x--;
      }

      return returnValue;
   }
}
