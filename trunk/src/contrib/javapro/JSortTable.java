/*
=====================================================================

  JSortTable.java
  
  Created by Claude Duguay
  Copyright (c) 2002
  
=====================================================================
*/

package contrib.javapro;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class JSortTable extends JTable
  implements MouseListener
{
  protected int sortedColumnIndex = -1;
  protected boolean sortedColumnAscending = true;
  
  public JSortTable(SortTableModel model)
  {
    super(model);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel)
  {
    super(model, colModel);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel,
    ListSelectionModel selModel)
  {
    super(model, colModel, selModel);
    initSortHeader();
  }

  protected void initSortHeader()
  {
    JTableHeader header = getTableHeader();
    header.setDefaultRenderer(new SortHeaderRenderer());
    header.addMouseListener(this);
  }

  public int getSortedColumnIndex()
  {
    return sortedColumnIndex;
  }
  
  public boolean isSortedColumnAscending()
  {
    return sortedColumnAscending;
  }
  
  public void mouseReleased(MouseEvent event)
  {
    TableColumnModel colModel = getColumnModel();
    int index = colModel.getColumnIndexAtX(event.getX());
    int modelIndex = colModel.getColumn(index).getModelIndex();
    
    SortTableModel model = (SortTableModel)getModel();
    if (model.isSortable(modelIndex))
    {
      // toggle ascension, if already sorted
      if (sortedColumnIndex == index)
      {
        sortedColumnAscending = !sortedColumnAscending;
      }
      sortedColumnIndex = index;
    
      model.sortColumn(modelIndex, sortedColumnAscending);
    }
  }
  
  public void mousePressed(MouseEvent event) {}
  public void mouseClicked(MouseEvent event) {}
  public void mouseEntered(MouseEvent event) {}
  public void mouseExited(MouseEvent event) {}
}

