package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import rero.config.*;
import rero.dck.*;

public class ListInput extends SuperInput implements ActionListener
{
   protected InputListModel  model;
   protected JList           list;
   protected StringList      data;

   protected String          desc;
   protected String         title;
 
   public ListInput(String variable, String _title, String _desc, int width, int height)
   {
      title = _title;
      desc  = _desc;

      setLayout(new BorderLayout());
      
      data  = ClientState.getClientState().getStringList(variable);
      data.load();

      model = new InputListModel();
      list  = new JList(model);

      JPanel temp = new JPanel();
      temp.setPreferredSize(new Dimension(width, height));

      add(temp, BorderLayout.EAST);

      temp = new JPanel();
      temp.setPreferredSize(new Dimension(width, height));

      add(temp, BorderLayout.WEST);

      add(new JScrollPane(list), BorderLayout.CENTER);
   
      JPanel buttons = new JPanel();
      buttons.setLayout(new FlowLayout(FlowLayout.CENTER));

      JButton addme = new JButton("Add");
      addme.setMnemonic('A');
      addme.addActionListener(this);
      buttons.add(addme);

      JButton remme = new JButton("Remove");
      remme.setMnemonic('R');
      remme.addActionListener(this);
      buttons.add(remme);

      add(buttons, BorderLayout.SOUTH);

      setMinimumSize(new Dimension(width, height));
   }

   public void save()
   {
      data.save();
   }

   public int getEstimatedWidth()
   {
      return 0;
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
      data.load();
      model.fireChange();
   }

   public void actionPerformed(ActionEvent ev)
   {
      if (ev.getActionCommand().equals("Remove"))
      {
          if (list.getSelectedIndex() >= 0)
          {
             data.getList().remove(list.getSelectedIndex());
             model.fireChange();
          }
      }

      if (ev.getActionCommand().equals("Add"))
      {
          String input = JOptionPane.showInputDialog(this, title, desc, JOptionPane.QUESTION_MESSAGE);
          if (input != null)
          {
             data.getList().add(input);
             model.fireChange();
          }
      }

      notifyParent();
   }

   protected class InputListModel extends AbstractListModel
   {
      public void fireChange()
      {
         model.fireContentsChanged(model, 0, model.getSize());
      }

      public Object getElementAt(int index)
      {
         return (String)data.getList().get(index);
      }

      public int getSize()
      {
         return data.getList().size();
      }
   }
}


