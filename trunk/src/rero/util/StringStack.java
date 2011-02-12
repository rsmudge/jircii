package rero.util;

public class StringStack
{
   protected String string;
   protected String delimeter;

   public StringStack(String data)
   {
      this(data, " ");
   }

   public StringStack(String data, String delim)
   {
      string = new String(data);
      delimeter = delim;
   }

   public void push(String element)
   {
      if (string.length() > 0)
      {
         string = string + delimeter + element;
      }
      else
      {
         string = element;
      }
   }

   public boolean isEmpty()
   {
      return string.length() == 0;
   }

   public String pop()
   {
      if (string.indexOf(delimeter) > -1)
      {
         String temp = string.substring(0, string.indexOf(delimeter));

         if (temp.length() >= string.length())
         {
            string = "";
            return temp;
         }
         string = string.substring(temp.length() + 1, string.length());
         return temp;
      }
      String temp = string;
      string = "";

      return temp;
   }

   public String toString()
   {
      return string;
   }

   public void setDelimeter(String delim)
   {
      delimeter = delim;
   }
}
