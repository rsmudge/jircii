package text.list;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import text.*;

import java.util.*;

public class ListSelectionSpace extends MouseInputAdapter
{
    protected ListData data;
    protected ListDisplay display;

    protected int         start;
  
    public ListSelectionSpace(ListDisplay _display, ListData _data)
    {
        data = _data;
        display = _display;
    }

    public void clearSelections()
    {
        Iterator i = data.dataIterator();
        while (i.hasNext())
        {
            ListElement element = (ListElement)i.next();
            element.setSelected(false);
        }
    }

    /** returns the list element for the *primary* selected element */
    public ListElement getSelectedElement()
    {
        ListElement temp = data.getElementAtLocation(start);
        if (temp != null && temp.isSelected())
        {
           return temp;
        }
 
        return null;
    }

    /** returns a linked list of all the selected elements (in order from top of the list to the bottom) */
    public LinkedList getSelectedElements()
    {
        LinkedList selection = new LinkedList();

        Iterator i = data.dataIterator();
        while (i.hasNext())
        {
            ListElement element = (ListElement)i.next();
            if (element.isSelected())
            {
               selection.add(element);
            }
        }

        return selection;
    }

    public void mousePressed(MouseEvent ev)
    {
        if (ev.getButton() == MouseEvent.BUTTON1 && !ev.isPopupTrigger() && !(ev.isShiftDown() && ev.isControlDown()) )
        {
           if ((ev.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK ||
               (ev.getModifiers() & MouseEvent.ALT_MASK) == MouseEvent.ALT_MASK)
           {
              ListElement temp = data.getElementAtLocation(ev.getY());

              if (temp != null)
              {
                 if (!temp.isSelected())
                 {
                    start = ev.getY();
                 }
                 temp.setSelected(!temp.isSelected());
                 display.repaint();
              }
           }
           else if ((ev.getModifiers() & MouseEvent.SHIFT_MASK) == MouseEvent.SHIFT_MASK)
           {
              selectRange(ev);
           }
           else
           {
              start = ev.getY();
              clearSelections();

              setSelectedAt(start, true);
              display.repaint();
           }
        }
    }

    public void mouseReleased(MouseEvent ev)
    {
    }

    public void mouseClicked(MouseEvent ev)
    {
    }

    public void mouseDragged(MouseEvent ev)
    {
       if (ev.getButton() == MouseEvent.BUTTON1_MASK || (ev.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK)
       {
          selectRange(ev);
       }
    }

    private void selectRange(MouseEvent ev)
    {
       clearSelections();

       boolean repaint = false;

       int _height = (TextSource.fontMetrics.getHeight() + 2);

       if (start < ev.getY())
       {
          int temp = start;
          while (temp < ev.getY())
          {
             setSelectedAt(temp, true);
             temp += _height;
          }
       }
       else
       {
          int temp = ev.getY();
          while (temp < start)
          {
             setSelectedAt(temp, true);
             temp += _height;
          }
          setSelectedAt(start, true);
       }

       display.repaint();
    }

    public boolean setSelectedAt(int pixely, boolean selected)
    {
       ListElement temp = data.getElementAtLocation(pixely);

       if (temp != null && temp.isSelected() != selected)
       {
          temp.setSelected(selected);
          return true;
       }
       return false;
    }

    public int translateToLineNumber(int pixely)
    {
       int _height = (TextSource.fontMetrics.getHeight() + 2);
       int lineNo = ((pixely - (pixely % _height)) / _height) + data.getValue();
       return lineNo;
    }
}
