package rero.dialogs.help;

import java.io.*;
import rero.config.*;
import java.util.*;

import rero.util.*;
import java.net.*;

public class HelpCommand
{
   protected String     command = "";
   protected String     description;
   protected String     example;
   protected LinkedList parms = new LinkedList();
   protected LinkedList descs = new LinkedList();

   protected String[] data;

   public HelpCommand(String[] _data)
   {
      data = _data;
   }

   public void init()
   {
      if (command.length() == data[0].length())
      {
         return;
      }

      command     = data[0];
      example     = data[1];
      description = data[2]; 

      for (int x = 3; x < data.length; x++)
      {
         String[] temp = data[x].split("\\s\\-\\s");
         
         if (temp[0].charAt(0) == '+')
         {
            parms.add("[" + temp[0].substring(1, temp[0].length()) + "]");
         }
         else
         {
            parms.add("&lt;" + temp[0] + "&gt;");
         }
 
         descs.add(temp[1]);
      }
   }

   public String toString()
   {
      init();
      
      StringBuffer returnValue = new StringBuffer();
      returnValue.append("<b><font color=\"#000099\">/");
      returnValue.append(command);
      returnValue.append("</b>");

      Iterator i, j;

      i = parms.iterator();
      while (i.hasNext())
      {
         returnValue.append(" ");
         returnValue.append(i.next().toString()); 
      }

      returnValue.append("<br><br>");
      returnValue.append(description);

      if (parms.size() > 0)
      {
         returnValue.append("<br><br><b><font color=\"#333333\">Parameters:</font></b>");
      }

      i = parms.iterator();
      j = descs.iterator();
      while (i.hasNext() && j.hasNext())
      {
         returnValue.append("<br>");
         returnValue.append(i.next().toString());
         returnValue.append(" - ");
         returnValue.append(j.next().toString()); 
      }


      returnValue.append("<br><br><b><font color=\"#333333\">Example:</font></b>\n<br>");
      returnValue.append(example);

      return returnValue.toString();
   }
}
