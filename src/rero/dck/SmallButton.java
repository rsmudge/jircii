package rero.dck;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public class SmallButton extends JLabel
{
   protected static String NORMAL = "<html><b>?</b></html>";
   protected static String ACTIVE = "<html><b>?</b></html>";

   protected JLabel label;

   protected LinkedList listeners;

   public SmallButton(Border border, String text)
   {
      super(NORMAL);

      setToolTipText(text);   
 
      addMouseListener(new TakeAction());

      label = this;

      listeners = new LinkedList();

      setBorder(border);
   }

   public void addActionListener(ActionListener l)
   {
      listeners.add(l);
   }

   public void fireEvent()
   {
      ActionEvent event = new ActionEvent(this, 0, "?");

      Iterator i = listeners.iterator();
      while (i.hasNext())
      {
         ((ActionListener)i.next()).actionPerformed(event);
      }
   }

   public class TakeAction extends MouseAdapter
   {
      protected Color original;

      public void mouseClicked(MouseEvent ev)
      {
         fireEvent();
      }
    
      public void mousePressed(MouseEvent ev)
      {
         original = label.getForeground();
         label.setForeground(UIManager.getColor("TextArea.selectionBackground"));
      }

      public void mouseReleased(MouseEvent ev)
      {
         label.setForeground(original);
      }

      public void mouseEntered(MouseEvent ev)
      {
         label.setText(ACTIVE);
      }

      public void mouseExited(MouseEvent ev)
      {
         label.setText(NORMAL);
      }
   }
}
