package rero.gui.windows;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import rero.util.*;

import contrib.javapro.*;  // sorted JTable code...

import rero.ircfw.interfaces.*;

import rero.config.*;
import rero.client.*;

import rero.gui.*;
import rero.gui.windows.*;
import rero.gui.toolkit.*;

import text.*;

import rero.dcc.*;
import rero.client.output.*;
import rero.util.*;

import sleep.bridges.*;
import sleep.runtime.*;
import rero.script.*;

public class ScriptedListDialog extends GeneralListDialog
{
   public ScriptedListDialog(String title, String hook, Object data, LinkedList cols)
   {
      super(title, hook, new ScriptedListModel(cols, data));
   }

   public void refreshData()
   {
      model.fireTableDataChanged();
   }

   public void init()
   {
      ((ScriptedListModel)model).install(popupHook, capabilities.getScriptCore());
   }

   public static class ScriptedCompare implements Comparator
   {
      private int     col;
      private boolean rev;

      public ScriptedCompare(int column, boolean reverse)
      {
         rev = reverse;
         col = column;
      }

      public int compare(Object a, Object b)
      {
         if (rev)
         {
            Object c = b;
            b = a;
            a = c;
         }

         String[] sa = a.toString().toLowerCase().split("\t");
         String[] sb = b.toString().toLowerCase().split("\t");
         
         try
         {
            int na = Integer.parseInt(sa[col]);
            int nb = Integer.parseInt(sb[col]);

            return na - nb;
         } 
         catch (Exception ex) { }
       
         return sa[col].compareTo(sb[col]);
      }
   }

   private static class ScriptedListModel extends GeneralListModel
   {
      private Scalar          data;
      private LinkedList      cols;
      private ScriptCore      script;
      private String          popupHook;

      public void install(String popup, ScriptCore s) { script = s; popupHook = popup; }

      public ScriptedListModel(LinkedList headers, Object scalar)
      {
         cols = headers;

         data = (Scalar)scalar;
      }

      public void sortColumn(int col, boolean ascending) 
      {
/*         String function = "&" + popupHook + "_sort";

         Stack temp = new Stack();
         temp.push(SleepUtils.getScalar((ascending ? 1 : 0)));
         temp.push(SleepUtils.getScalar(col));

         script.callFunction(function, temp); */

         data.getArray().sort(new ScriptedCompare(col, ascending));
         fireTableDataChanged();
      }

      public HashMap getEventHashMap(int row) 
      { 
         return ClientUtils.getEventHashMap(row+"", ""); 
      }

      public int getRowCount()
      {
         return data.getArray().size();
      }

      public int getColumnCount()
      {
         return cols.size();
      }

      public int getColumnWidth(int col)
      {
         return (int)(TextSource.fontMetrics.stringWidth(cols.get(col).toString()) * 1.5);
      }

      public String getColumnName(int col)
      {
         return cols.get(col).toString();
      }

      public Object getValueAt(int row, int col)
      {
         if (row < getRowCount() && col < getColumnCount())
         {
            String temp = data.getArray().getAt(row).toString();
            String blah[] = temp.split("\t");

            if (col < blah.length)
            {
               AttributedString tempa = AttributedString.CreateAttributedString(blah[col]);
               tempa.assignWidths();
 
               return tempa;
            }
         }

         return null;
      }

      public boolean isSortable(int col)
      {
         return true;
      }
   }
}
