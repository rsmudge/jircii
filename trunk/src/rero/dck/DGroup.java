package rero.dck;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

public abstract class DGroup extends DContainer implements DItem
{
    protected String title;
    protected int    inset;
    protected boolean useBorder = true;

    public DGroup (String _title)
    {
       this(_title, 0);
    }

    public DGroup (String _title, int _inset)
    {
       title = " " + _title + " ";
       inset = _inset;
    } 

    // No border or title; for alignment purposes, since the layout code isn't too flexible.
    public DGroup (int _inset)
    {
	    useBorder = false;
	    inset = _inset;
    }

    public DGroup () { this(0); }

    public int  getEstimatedWidth()
    {
       return 0; 
    }

    public void setAlignWidth(int width) { }

    public JComponent setupLayout(JComponent component)
    {
       component.setLayout(new BorderLayout());
       
       JPanel temp = new JPanel();
       temp.setPreferredSize(new Dimension(inset, 0));

       component.add(temp, BorderLayout.EAST);

       temp = new JPanel();
       temp.setPreferredSize(new Dimension(inset, 0));

       component.add(temp, BorderLayout.WEST);

       JPanel child = new JPanel();
       if (useBorder)
           child.setBorder(BorderFactory.createTitledBorder(getTitle()));
       child.setLayout(new GridBagLayout());

       component.add(child, BorderLayout.CENTER);

       return child;
    }

    public String getTitle()
    {
       return title;
    }
  
    public JComponent getComponent()
    {
       return getDialog();
    }
}
