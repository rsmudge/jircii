package rero.dck;

import javax.swing.*;

public interface DItem
{
   public void setEnabled(boolean b);
   public void save(); 
   public int  getEstimatedWidth();
   public void setAlignWidth(int width);
   public void setParent(DParent parent);
   public JComponent getComponent();
   public void refresh();
}
