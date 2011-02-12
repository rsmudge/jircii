package text.wrapped;

import text.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import rero.config.*;

public class WrappedData implements BoundedRangeModel
{
    private static int WRAP_TOLERANCE = 2000;
    private static int WRAP_TO        = 1000;

    static
    {
       WRAP_TOLERANCE = ClientState.getClientState().getInteger("ui.buffersize", ClientDefaults.ui_buffersize);
       WRAP_TO        = WRAP_TOLERANCE / 2;
    }

    protected WrappedContainer last = null; // last element in our linked list of wrapped text containers
    protected WrappedContainer text = null; // paint from this point on..
    protected WrappedContainer head = null; // head element in our linked list of wrapped text containers

    protected int tempMax      = 1; 
    protected int maxValue     = 1;
    protected int currentValue = 0;

    protected boolean adjusting = false;

    protected LinkedList listeners = new LinkedList();
    protected ChangeEvent event;

    protected int extent     = 1;
    protected int tempExtent = 1;

    public WrappedData()
    {
        event = new ChangeEvent(this);
    }

    public ListIterator find(String text)
    {
        LinkedList values = new LinkedList();

        int x = 0;

        WrappedContainer temp = last;
        while (temp != null)
        {
           if (temp.getText().toUpperCase().indexOf(text.toUpperCase()) > -1)
           {
               values.add(new Integer(x));
           }

           x++;
           temp = temp.prev;
        }

        ListIterator i = values.listIterator();

        while (i.hasNext())
        {
           int tempzz = ( (Integer)i.next() ).intValue();

           if (tempzz >= currentValue) 
           { 
              i.previous();
              break; 
           }
        }

        return i;
    }

    public void dirty()
    {
        WrappedContainer temp = last;
        while (temp != null)
        {
           temp.reset(); // resets its parameters, tells it that we are now "dirty"
           temp = temp.previous();
        }
    }

    public void reset()
    {
        tempMax      = 1;
        maxValue     = 1;
        currentValue = 0;
        extent       = 1;
        tempExtent   = 1;
        head         = null;
        last         = null;
        text         = null; 
        fireChangeEvent();
    }

    public WrappedContainer getCurrentText()
    {
        return text;
    }

    public WrappedObject getTokenAt(int height, int pixelx, int pixely)
    {
       int baseline = height - 5; // gives us a 5 pixel buffer
                                  // between the textbox and the textarea

       int lineno  = 0;
       int desired = TextSource.translateToLineNumber(pixely);

       WrappedContainer head = text;
       AttributedText[] strings;

       while (head != null && baseline > 0)
       {
           strings = head.getWrappedText();

           for (int x = 0; strings != null && x < strings.length && baseline > 0; x++)
           {
              if (pixely >= (baseline - TextSource.fontMetrics.getHeight() - 2))
              {
                 return new WrappedObject(head.getText(), head.getTokenAt(strings[x], pixelx));
              }
              lineno++;
              baseline -= (TextSource.fontMetrics.getHeight() + 2);
           }

           head = head.next();
        }
         
        return null;
    } 

    public WrappedObject getAttributesAt(int height, int pixelx, int pixely)
    {
       int baseline = height - 5; // gives us a 5 pixel buffer
                                  // between the textbox and the textarea

       int lineno  = 0;
       int desired = TextSource.translateToLineNumber(pixely);

       WrappedContainer head = text;
       AttributedText[] strings;

       while (head != null && baseline > 0)
       {
           strings = head.getWrappedText();

           for (int x = 0; x < strings.length && baseline > 0; x++)
           {
              if (pixely >= (baseline - TextSource.fontMetrics.getHeight() - 2))
              {
                 return new WrappedObject(head.getText(), head.getAttributedTextAt(strings[x], pixelx));
              }
              lineno++;
              baseline -= (TextSource.fontMetrics.getHeight() + 2);
           }

           head = head.next();
        }
         
        return null;
    } 

    protected Runnable changeEventTask = new Runnable()
    {
        public void run() 
        {
           fireChangeEvent();
        }
    };

    public void fireChangeEvent()
    {
        ListIterator i = listeners.listIterator();
        while (i.hasNext())
        {
           ChangeListener temp = (ChangeListener)i.next();
           temp.stateChanged(event);
        }
    }

    public void addChangeListener(ChangeListener x)
    {
        listeners.add(x);
    }

    public void removeChangeListener(ChangeListener x)
    {
        listeners.remove(x);
    }

    public void addText(WrappedContainer container)
    {
        boolean jump = true;

        if (head == null)
        {
           head = container;
           text = container;
           last = container;
        }
        else
        {
           container.setNext(head);
           head.setPrevious(container);

           head = container;

           if (text.previous() != head)  // if the current text has a previous text, then we're scrolled up
           {
              maxValue++;              
           }
           else if (!getValueIsAdjusting()) 
           {
              text = container;

              if (maxValue > WRAP_TOLERANCE)
              {
                 while (maxValue > WRAP_TO && last.hasPrevious())
                 {
                    last = last.previous();
                    maxValue--;
                 } 
                 last.setNext(null);
              }

              currentValue = maxValue;
              maxValue++;
           }
           else
           {
              tempMax++;
           }
        }

        SwingUtilities.invokeLater(changeEventTask); // this is in place to prevent a case of deadlock
    }

    public int getExtent()
    { 
        return 1; 
    }

    public int getMaximum()
    {
        return maxValue;
    }

    public int getMinimum()
    {
        return 0;
    }

    public int getValue()
    {
        return currentValue;
    }

    public boolean getValueIsAdjusting()
    {
        return adjusting;
    }

    public void setExtent(int x)
    {
        // do a check on is adjusting if we're going to choose wether or not to update the extent.

        extent = x;
    }

    public void setMaximum(int x)
    {
    }

    public void setMinimum(int x)
    {
    }

    public void setRangeProperties(int newValue, int extent, int min, int max, boolean adjusting)
    {
    }

    public void setValue (int newValue)
    {
        if (head == null)
            return;

        if (currentValue > newValue)
        {
             // move down to new value using next values           

            while (currentValue > newValue && text.hasNext())
            {
                text = text.next();
                currentValue--;
            }
        }
        else if (currentValue < newValue)
        {
            // move up to new value using prev values;

            while (currentValue < newValue && text.hasPrevious())
            {
                text = text.previous();
                currentValue++;
            }
        }

        currentValue = newValue;

        if (currentValue >= maxValue)
        {
            currentValue = maxValue - 1;
        }
        if (currentValue < 0)
        {
            currentValue = 0;
        }

        //System.out.println(currentValue + " versus possible is: " + 
//maxValue + " and extent is: " + extent);

        fireChangeEvent();
    }

    public void setValueIsAdjusting(boolean b)
    {
        adjusting = b;
        if (!b && tempMax > maxValue)
        {
           maxValue   = tempMax;
        }
        else
        {
           tempMax    = maxValue;
        } 
    }

    protected SelectionSpace selection;

    public void setSelection(SelectionSpace select)
    {
        selection = select;
    }

    public SelectionSpace getSelection()
    {
        return selection;
    }
}


