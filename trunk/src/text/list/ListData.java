package text.list;

import text.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public abstract class ListData implements BoundedRangeModel
{
    protected ChangeEvent event;
    protected LinkedList listeners;

    protected int currentValue  = 0;

    protected boolean adjusting = false;

    protected int extent = 1;

    public ListData()
    {
       event = new ChangeEvent(this);
       listeners = new LinkedList();
    }

    public ListElement getElementAtLocation(int pixely)
    {
       int _height = (TextSource.fontMetrics.getHeight() + 2);

       int lineNo = ((pixely - (pixely % _height)) / _height) + currentValue;       

       return getElementAt(lineNo);
    } 

    public void dirty() { }

    public abstract int getSize();

    public abstract ListElement head();
    public abstract ListElement next();

    public abstract ListElement getElementAt(int number);
    public abstract Iterator    dataIterator();

    public Object getSynchronizationKeyOuter()
    {
        return null;
    }

    public Object getSynchronizationKeyInner()
    {
        return null;
    }

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

    public int getExtent()
    { 
        return extent; 
    }

    public int getMaximum()
    {
        return getSize();
    }

    public int getMinimum()
    {
        return 0;
    }

    public int getValue()
    {
        if ( (currentValue + extent) > getSize() )
        {
           currentValue = getSize() - extent;
        }

        if (getSize() < extent)
        {
           currentValue = 0;
        }

        if (currentValue < getSize())
        {
           return currentValue;
        } 

        currentValue = getSize() - 1; 
        return currentValue;
    }

    public boolean getValueIsAdjusting()
    {
        return adjusting;
    }

    public void setExtent(int x)
    {
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
        if (newValue < 0)
        {
           currentValue = 0;
        }
        else if (newValue < getSize())
        {
           currentValue = newValue;
        }
        else
        {
           currentValue = getSize() - 1;
        }

        fireChangeEvent();
    }

    public void setValueIsAdjusting(boolean b)
    {
        adjusting = b;
    }
}


