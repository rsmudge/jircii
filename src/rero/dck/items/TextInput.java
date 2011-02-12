package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import java.io.*;

import rero.dck.*;
import rero.config.*;

public class TextInput extends SuperInput implements ActionListener
{
   protected JEditorPane   text;

   public TextInput(String _variable, int inset)
   {
      text   = new JEditorPane();
      setLayout(new BorderLayout(2, 2));

      add(new JScrollPane(text), BorderLayout.CENTER);

      variable = _variable;

      setBorder(BorderFactory.createEmptyBorder(0, inset, 0, inset));
   }

   public void actionPerformed(ActionEvent ev)
   {
      text.setText("");
   }

   public void save()
   {
      String[] blah = text.getText().split("\n");

      StringList temp = new StringList(getVariable());
      temp.clear();

      for (int x = 0; x < blah.length; x++)
      {
         temp.add(blah[x]);
      }

      temp.save();
   }

   public int getEstimatedWidth()
   {
      return -1;
   }

   public void setAlignWidth(int width)
   {
   }

   public JComponent getComponent()
   {
      return this;
   }

   public void refresh()
   {
      StringList string = ClientState.getClientState().getStringList(getVariable());
      StringBuffer data = new StringBuffer();

      Iterator i = string.getList().iterator();
      while (i.hasNext())
      {
         data.append(i.next());
         data.append("\n");
      }
      
      text.setText(data.toString());
      text.setCaretPosition(0);
   }
}


