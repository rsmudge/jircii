package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.dck.*;

public class LabelInput extends JPanel implements DItem
{
   protected JTextPane label;

   public LabelInput(String text, int width)
   {
      setLayout(new BorderLayout());

      label = new JTextPane();
      label.setText(text);
      label.setBorder(null);
      label.setOpaque(false);
      label.setEditable(false);
      
      add(label, BorderLayout.CENTER);
     
      JPanel gap = new JPanel();
      gap.setPreferredSize(new Dimension(width, 0));
      add(gap, BorderLayout.EAST);

      gap = new JPanel();
      gap.setPreferredSize(new Dimension(width, 0));
      add(gap, BorderLayout.WEST);
   }

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

   public void setText(String text)
   {
      label.setText(text);
   }

   public JComponent getComponent()
   {
      return this;
   }
}

