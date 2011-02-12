package rero.gui.toolkit;

import java.util.*;
import javax.swing.table.*;

import java.awt.event.*;

import text.*;

import contrib.javapro.*;

/** A model for the sortable list window thingy... */
public abstract class GeneralListModel extends AbstractTableModel implements SortTableModel
{
   public boolean isSortable(int col)
   {
      return true;
   }

   public abstract void sortColumn(int col, boolean ascending);

   public abstract int getRowCount();
   public abstract int getColumnCount();

   public abstract String getColumnName(int col);
   public abstract int getColumnWidth(int col);

   public abstract HashMap getEventHashMap(int row);

   /** make sure this method always returns an AttributedString */
   public abstract Object getValueAt(int row, int col);
}
