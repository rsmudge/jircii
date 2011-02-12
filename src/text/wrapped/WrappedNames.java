package text.wrapped;

import text.*;

import java.util.*;
import java.awt.*;
import rero.util.*;

/* a special class for dealing with the formatting of /names output */

public class WrappedNames extends WrappedContainer
{
   protected AttributedText[] rawData;
   protected double           percentage;
   protected String[]         users;

   public WrappedNames(String text, String[] _users, double _percentage)
   {
      super(text);

      users   = _users;

      calculateAttributes();

      percentage = _percentage;
   }

   public void calculateAttributes()
   {
      rawData = new AttributedText[users.length];

      for (int x = 0; x < users.length; x++)
      {
         AttributedString temp = AttributedString.CreateAttributedString(users[x]);
         temp.assignWidths();
         rawData[x] = temp.getAttributedText(); 
      }
   }

   public void touch(int size)
   {
      int width = (int)(size * percentage);
      if (width < maxSize && width > minSize)
      {
         return;
      } 

      wrapped = wrapNames(width); 
      determineBounds(width);
   }

   public void reset()
   {
      super.reset();
      calculateAttributes();
   }

   public AttributedText[] wrapNames(int width)
   {
      LinkedList     values = new LinkedList();
     
      AttributedText head = rawData[0].cloneList();

      AttributedText temp = head; // temp is essentially the tail of the new list for our wordwrapped line of text
      while (temp.next != null) { temp = temp.next; }

      int         current = rawData[0].getWidth();
 
      for (int x = 1; x < rawData.length; x++)
      {
         current += rawData[x].getWidth();
         
         if (current > width)
         {
            values.add(head);
            head      = rawData[x].cloneList();
            current   = head.getWidth();
            temp      = head;
            while (temp.next != null) { temp = temp.next; }
         }
         else
         {
            temp.next = rawData[x].cloneList();
            while (temp.next != null) { temp = temp.next; }
         }
      }

      values.add(head);

      //
      // convert the linked list to an array.
      //
      AttributedText returnValue[] = new AttributedText[values.size()];
      int x = values.size() - 1;
      ListIterator i = values.listIterator();
      while (i.hasNext())
      {
         returnValue[x] = (AttributedText)i.next();
         x--;
      }

      return returnValue;
   }
}
