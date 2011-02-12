package text;

import text.list.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.font.*;
import java.awt.datatransfer.*;

import text.event.*;

import rero.config.*;

public class ListDisplay extends JComponent implements MouseWheelListener, MouseInputListener, ClientStateListener
{
   protected ListDisplayComponent display;
   protected JScrollBar           scroller;

   protected ListData             data;
   protected ListSelectionSpace   select;

   public ListElement getSelectedElement()
   {
      return select.getSelectedElement();
   }

   public LinkedList getSelectedElements()
   {
      return select.getSelectedElements();
   }

   public void propertyChanged(String a, String b) { data.dirty(); } // happens when ui.font changes...

   public void mouseWheelMoved(MouseWheelEvent e)
   {
      // manipulate the scroll bar directly for this one..

      if (e.getWheelRotation() >= 0) // up
      {
          scroller.setValue(scroller.getValue() + (e.getScrollAmount()));
          repaint();
      }
      else // down
      {
          scroller.setValue(scroller.getValue() - (e.getScrollAmount()));
          repaint();
      }
   }

   public ListDisplay(ListData data)
   {
      scroller = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
      display  = new ListDisplayComponent();

      select = new ListSelectionSpace(this, data);
      addMouseListener(select);
      addMouseMotionListener(select);

      this.data = data;

      display.installDataSource(data);
      scroller.setModel(data);

      addMouseWheelListener(this);
      addMouseListener(this);
      addMouseMotionListener(this);
   
      data.addChangeListener(display);

      setLayout(new BorderLayout());
      add(scroller, BorderLayout.EAST);
      add(display, BorderLayout.CENTER);

      setOpaque(false);
      setDoubleBuffered(false);

      ClientState.getClientState().addClientStateListener("ui.font", this);
   }

   public void mousePressed(MouseEvent ev)
   {
   }   

   public void mouseReleased(MouseEvent ev)
   {
   }   

   public void mouseClicked(MouseEvent ev)
   {
      if (ev.isShiftDown() && ev.isControlDown())
      {
          ListElement temp = data.getElementAtLocation(ev.getY());
          if (temp != null)
          {
              AttributedText attribs = temp.getAttributedText().getAttributesAt(ev.getX());
              if (attribs != null)
              {
                 ModifyColorMapDialog.showModifyColorMapDialog(this, attribs.foreIndex);
                 repaint();
                 ev.consume();
              }
          }
      }
   }   

   public void mouseEntered(MouseEvent ev)
   {
      // we have nothing to do for this event...
   }   

   public void mouseExited(MouseEvent ev)
   {
      // nothing to do for this event...
   }   

   public void mouseDragged(MouseEvent ev)
   {
   }   

   public void mouseMoved(MouseEvent ev)
   {
      // again nothing to do for this event...
   }   

/*   protected void finalize()
   {
      System.out.println("Finalizing the List Display");
   } */
}



