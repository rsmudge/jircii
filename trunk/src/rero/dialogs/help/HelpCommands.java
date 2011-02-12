package rero.dialogs.help;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;

import rero.config.*;
import rero.dialogs.*;
import rero.gui.*;

import java.net.*;

import java.util.*;

public class HelpCommands extends HelperObject implements ListSelectionListener
{
    public void valueChanged(ListSelectionEvent e) 
    {
        if (e.getValueIsAdjusting())
 	   return;

        JList theList = (JList)e.getSource();
        String key = (String)(theList.getSelectedValue());

	if (theList.isSelectionEmpty() || key.equals(" ")) 
	{
           updateText("");
	} 
	else 
        {
           updateText(help.getCommand(key));
	}
    }

    public JComponent getNavigationComponent()
    {
	JList comOptions = new JList(help.getCommandData().getData());
        comOptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        comOptions.addListSelectionListener(this);

        return comOptions;
    }
}



