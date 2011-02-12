/*
=====================================================================

  SortTableModel.java
  
  Created by Claude Duguay
  Copyright (c) 2002
  
=====================================================================
*/

package contrib.javapro;

import javax.swing.table.*;

public interface SortTableModel
  extends TableModel
{
  public boolean isSortable(int col);
  public void sortColumn(int col, boolean ascending);
}

