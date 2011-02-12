package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.config.*;
import rero.dck.*;

import java.util.*;

public class TabbedInput extends JPanel implements DItem
{
   protected LinkedList  items = new LinkedList();
   protected JTabbedPane tabs;

   public TabbedInput()
   {
      setLayout(new BorderLayout());

      tabs = new JTabbedPane();
      add(tabs, BorderLayout.CENTER);
   }

   public void addTab(DTab item)
   {
      tabs.addTab(item.getTitle(), null, item.getDialog(), item.getDescription());
      items.add(item);
   }

   public Dimension getPreferredSize()
   {
      return new Dimension(0, (int)super.getPreferredSize().getHeight());
   }

   public void setEnabled(boolean b)
   {
      Iterator i = items.iterator();
      while (i.hasNext())
      {
         ((DContainer)i.next()).setEnabled(b);
      }

      tabs.setEnabled(b);
   }

   public void save()
   {
      Iterator i = items.iterator();
      while (i.hasNext())
      {
         ((DContainer)i.next()).save();
      }
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
      Iterator i = items.iterator();
      while (i.hasNext())
      {
         ((DContainer)i.next()).setParent(parent);
      }
   }

   public JComponent getComponent()
   {
      return this;
   }

   public void refresh()
   {
      int count = 0;
      Iterator i = items.iterator();
      while (i.hasNext())
      {
         DTab item = (DTab)i.next();        
         item.refresh();

         tabs.setEnabledAt(count, item.isEnabled());
         item.setEnabled(item.isEnabled());

         count++;
      }
   }
}


