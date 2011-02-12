package text;

import text.wrapped.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.font.*;
import java.awt.datatransfer.*;

import rero.config.*;
import rero.gui.script.WindowAreaClickListener;
import rero.gui.windows.ChannelWindow;
import rero.gui.SessionManager;

import text.event.*;

public class WrappedDisplay extends JComponent implements MouseWheelListener, MouseInputListener, ClientStateListener
{
   protected WrappedDisplayComponent display;
   protected JScrollBar              scroller;
   protected WrappedData             data;
   protected FindDialog              search = null;

   public void showSearchDialog(String title)
   {
      if (search == null)
      {
         search = new FindDialog(this, title, this, "");
      }

      search.showDialog();
   }

   public void addText(String text)
   {
      data.addText(new WrappedContainer(text));
   }

   public void addTextTable(String single, String text[], double percentage)
   {
      data.addText(new WrappedNames(single, text, percentage));
   }

   public void propertyChanged(String a, String b)
   {
      data.dirty(); // invalidates all stored text
   }

   public void clear()
   {
      data.reset();
   }

   /** returns a linked list of integers indicating which lines the text was found at */
   public ListIterator find(String text)
   {
      return data.find(text);
   }

   public void scroll(int scrollSize)
   {
      data.setValue(data.getValue() + scrollSize);
      repaint();
   }

   public void scrollTo(int specifics)
   {
      data.setValue(specifics);
      repaint();
   }

   public void mouseWheelMoved(MouseWheelEvent e)
   {
      int scrollSize = e.getScrollAmount() - 1;
      if (scrollSize <= 0) { scrollSize = 1; }

      if (e.getWheelRotation() >= 0) // up
      {
          data.setValue(data.getValue() + scrollSize);
          repaint();
      }
      else // down
      {
          data.setValue(data.getValue() - scrollSize);
          repaint();
      }
   }

   public WrappedDisplay()
   {
      scroller = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
      display  = new WrappedDisplayComponent();

      data     = new WrappedData();

      display.setWrappedData(data);
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

//      setBorder(null);
      setBorder(BorderFactory.createEmptyBorder(0, TextSource.UNIVERSAL_TWEAK, 0, 0));

      ClientState.getClientState().addClientStateListener("ui.font", this);
   }

   public void mousePressed(MouseEvent ev)
   {
      if (ev.isPopupTrigger()) { data.setValueIsAdjusting(false); return; }

      data.setValueIsAdjusting(true);
      data.setSelection(new SelectionSpace(ev.getPoint()));
      repaint();
   }   

   public void mouseReleased(MouseEvent ev)
   {
      data.setValueIsAdjusting(false);

      if (ev.isPopupTrigger()) { return; }

      if (data.getSelection() != null && data.getSelection().getSelectedText() != null && data.getSelection().getSelectedText().length() > 0)
      {
         StringSelection selected = new StringSelection(data.getSelection().getSelectedText());

         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selected, selected);

         if (Toolkit.getDefaultToolkit().getSystemSelection() != null)
         {
            Toolkit.getDefaultToolkit().getSystemSelection().setContents(selected, selected);
         }
      }

      data.setSelection(null);
      repaint();
   }   

   public void mouseClicked(MouseEvent ev)
   {
      if (ev.isPopupTrigger()) { data.setValueIsAdjusting(false); return; }

      if (ev.isShiftDown())
      {
         WrappedObject temp = data.getAttributesAt(display.getHeight(), ev.getX(), ev.getY());

         if (temp != null && temp.getObject() != null)
         {
            AttributedText attribs = (AttributedText)(temp.getObject());
            int            index;
     
            if (ev.isControlDown() && attribs.backIndex > -1)
            {
               index = attribs.backIndex;
            }
            else
            {
               index = attribs.foreIndex;
            }
            
            ModifyColorMapDialog.showModifyColorMapDialog(this, index);
         } 
         return;
      }

      WrappedObject temp = data.getTokenAt(display.getHeight(), ev.getX(), ev.getY());

      if (temp != null && temp.getObject() != null)
      {
         if (fireClickEvent(temp.getObject().toString(), temp.getText(), ev).isAcknowledged())
         {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            new Thread(new Runnable()
            {
               public void run()
               {
                  try {
                  Thread.sleep(300);
                  setCursor(Cursor.getDefaultCursor()); } catch (Exception zex) { zex.printStackTrace(); }
               }
            }).start();
         }
      } else {
        String wname = SessionManager.getGlobalCapabilities().getActiveSession().getActiveWindow().getName();
        fireClickEvent(null, wname, ev);
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
      if (ev.isPopupTrigger()) { return; }

      if (data.getSelection() != null)
      {
         data.getSelection().growSelection(ev.getPoint());
         Rectangle temp = data.getSelection().getChangedArea();
         repaint(temp);
      }
   }   

   public void mouseMoved(MouseEvent ev)
   {
      // again nothing to do for this event...
   }   

   protected LinkedList listeners = new LinkedList();

   public void addClickListener(ClickListener l)
   {
      listeners.add(l);
   }

   public ClickEvent fireClickEvent(String text, String context, MouseEvent ev)
   {
      ClickEvent event = new ClickEvent(text, context, ev);

      ListIterator i = listeners.listIterator();
      while (i.hasNext() && !event.isConsumed())
      {
         ClickListener l = (ClickListener)i.next();
        // pass events with null text only to area click listener
        if(text == null && !(l instanceof WindowAreaClickListener)) continue;
        l.wordClicked(event);
      }

      return event;
   }

/*   protected void finalize()
   { 
      System.out.println("Finalizing the wrapped display");
   } */
}



