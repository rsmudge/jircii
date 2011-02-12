package rero.config;

import java.util.*;

public class StringList
{
   protected LinkedList values;
   protected String     key;

   public StringList(String _key)
   {
      key = _key;
      load();
   }

   public void load()
   {
      String     value = ClientState.getClientState().getString(key, null);
      LinkedList rv    = new LinkedList();

      if (value != null)
      {
         String[] temp = value.split("::");
         for (int x = 0; x < temp.length; x++)
         {
            if (temp[x].length() > 0)
               rv.add(temp[x]);
         }
      }

      values = rv;
   }

   public boolean isValue(String value)
   {
      return values.contains(value);
   }

   public LinkedList getList()
   {
      return values;
   }

   public void save()
   {
      StringBuffer value = new StringBuffer();

      if (values.size() > 0)
      {
         value.append(values.getFirst());

         Iterator i = values.listIterator(1);
         while (i.hasNext())
         {
            value.append("::");
            value.append(i.next().toString());
         }      
      }

      ClientState.getClientState().setString(key, value.toString());
   }

   public void add(String element)
   {
      values.add(element);
   }

   public void remove(String element)
   {
      Iterator i = values.listIterator();
      while (i.hasNext())
      {
         if (i.next().toString().equals(element))
         {
            i.remove();
         }
      }
      
      save();
      ClientState.getClientState().fireChange(key, element);
   }

   public void clear()
   {
      values = new LinkedList();
      save();
      ClientState.getClientState().fireChange(key);
   }
}
