package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.dck.*;

public class BlankInput extends JPanel implements DItem
{
   public void setEnabled(boolean b)
   {
   }

   public void save()
   {

   }

   public void refresh()
   {
   }

   public int getEstimatedWidth()
   {
      return 0;
   }

   public void setAlignWidth(int width)
   {
   }

   public void setParent(DParent parent)
   {
      
   }

   public JComponent getComponent()
   {
      return this;
   }

   public Dimension getPreferredSize()
   {
      return new Dimension(0, 5);
   }
}

