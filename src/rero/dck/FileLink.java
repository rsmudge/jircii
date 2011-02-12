package rero.dck;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public class FileLink extends JComponent
{
   protected LinkedList listeners;
   protected JComponent label;

   public FileLink()
   {
      addMouseListener(new TakeAction());
      listeners = new LinkedList();

      label = this;
   }

   protected String text = "";

   public void setText(String _text) { text = _text; setToolTipText(text); repaint(); }
   public String getText() { return text; }

   public Dimension getPreferredSize()
   {
       return new Dimension(0, Toolkit.getDefaultToolkit().getFontMetrics(getFont()).getHeight());
   }

   public void paint(Graphics g)
   {
      StringBuffer string = new StringBuffer();
      FontMetrics  fm     = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());

      int x;
      for (x = 0; x < text.length() && fm.stringWidth(text.substring(0, x)) < getWidth(); x++);

      if (isEnabled())
      {
         g.setColor(label.getForeground());
         g.drawLine(0, getHeight() - fm.getDescent() + 1, fm.stringWidth(text.substring(0, x)), getHeight() - fm.getDescent() + 1);
      }
      else
      {
         g.setColor(label.getForeground().brighter());
      }

      g.drawString(getText().substring(0, x), 0, getHeight() - fm.getDescent());
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
         if (isEnabled())
            fireEvent();
      }
    
      public void mousePressed(MouseEvent ev)
      {
         if (isEnabled())
         {
            original = label.getForeground();
            label.setForeground(UIManager.getColor("TextArea.selectionBackground"));
            label.repaint();
         }
      }

      public void mouseReleased(MouseEvent ev)
      {
         if (isEnabled())
         {
            label.setForeground(original);
            label.repaint();
         }
      }

      public void mouseEntered(MouseEvent ev)
      {
      }

      public void mouseExited(MouseEvent ev)
      {
         if (isEnabled())
         {
            label.setForeground(original);
            label.repaint();
         }
      }
   }
}
