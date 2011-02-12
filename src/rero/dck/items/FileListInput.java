package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import rero.config.*;
import rero.dck.*;

import javax.swing.filechooser.*;

import java.util.*;
import java.io.*;

public class FileListInput extends SuperInput implements ActionListener, ListSelectionListener
{
   protected InputListModel  model;
   protected JList           list;
   protected StringList      data;
   protected JTextField      fullPath;

   protected JButton         addme, remme;

   protected String          desc;
 
   protected JFileChooser   chooser;

   public FileListInput(String variable, String _desc, String add, char mn1, String rem, char mn2, int width, int height)
   {
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

      JLabel l_path = new JLabel("Selected:  ");

      fullPath = new JTextField("no file selected");
      fullPath.setBorder(null);
      fullPath.setEditable(false);
      fullPath.setOpaque(false);

      JPanel pathDisplay = new JPanel();
      pathDisplay.setLayout(new BorderLayout());
      pathDisplay.add(l_path, BorderLayout.WEST);
      pathDisplay.add(fullPath, BorderLayout.CENTER);

      pathDisplay.setBorder(BorderFactory.createEmptyBorder(0, width, 0, 0));

      addme = new JButton(add);
      addme.setMnemonic(mn1);
      addme.addActionListener(this);
      buttons.add(addme);

      remme = new JButton(rem);
      remme.setMnemonic(mn2);
      remme.addActionListener(this);
      buttons.add(remme);

      JPanel evil = new JPanel();
      evil.setLayout(new BorderLayout());
      evil.add(pathDisplay, BorderLayout.NORTH);
      evil.add(buttons, BorderLayout.SOUTH);
  
      list.addListSelectionListener(this);

      add(evil, BorderLayout.SOUTH);

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

   public void valueChanged(ListSelectionEvent ev)
   {
      if (!ev.getValueIsAdjusting())
      {
         setSelectedCaption();
      }
   }

   public void setSelectedCaption()
   {
      if (list.getSelectedIndex() > -1 && list.getSelectedIndex() < list.getModel().getSize())
      {
         fullPath.setText(data.getList().get(list.getSelectedIndex()).toString());
      }     
      else
      {
         fullPath.setText("no file selected");
      }
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
      if (ev.getSource() == remme)
      {
          if (list.getSelectedIndex() >= 0)
          {
             data.getList().remove(list.getSelectedIndex());
             model.fireChange();
          }
      }

      if (ev.getSource() == addme)
      {
          if (chooser == null)
          {
             chooser = new JFileChooser();
             chooser.setApproveButtonText(desc);
          }

          if (chooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION)
          {
             data.getList().add(chooser.getSelectedFile().getAbsolutePath());
             model.fireChange();
          }
      }

      setSelectedCaption();
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
         return new File((String)data.getList().get(index)).getName();
      }

      public int getSize()
      {
         return data.getList().size();
      }
   }
}


