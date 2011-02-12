package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;

import rero.dck.*;
import rero.config.*;

import rero.dialogs.toolkit.*;
import rero.dialogs.server.*;

import java.util.*;

public class NetworkSelect extends SuperInput implements ItemListener
{
   public static final String ALL_NETWORKS = "All Networks";

   protected JComboBox  networks;
   protected JButton    add;
   protected JButton    delete;

   protected String     networkV;
   protected String     currentV;
   protected StringList data;

   public NetworkSelect(String _variableNetworks, String _variableCurrent)
   {
      networkV = _variableNetworks;
      currentV = _variableCurrent;

      setLayout(new BorderLayout()); 

      add(new JLabel(" Network:"), BorderLayout.NORTH);

      networks  = new JComboBox();
      networks.setPrototypeDisplayValue("SuperLamerNet");

      networks.addItemListener(this);

      JPanel bottom = new JPanel();
      bottom.setLayout(new FlowLayout(FlowLayout.LEFT));

      bottom.add(networks);
      
      add    = new JButton("Add");
      add.setMnemonic('A');
      add.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent ev)
         {
            showNetworkDialog();
         }
      });

      delete = new JButton("Delete");
      delete.setMnemonic('D');

      delete.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent ev)
         {
            if (networks.getSelectedIndex() > 0) // we don't want to remove the first element, so > 0...
            {
               ClientState.getClientState().setString(getVariable(), "");
               ClientState.getClientState().sync();

               int zz = networks.getSelectedIndex() - 1;
               data.remove(networks.getSelectedItem().toString());
               refresh();
               networks.setSelectedIndex(zz);
            }
         }
      });
 
      bottom.add(add);
      bottom.add(delete);

      add(bottom, BorderLayout.CENTER);
   }

   public void showNetworkDialog()
   {
       NetworkPanel panel = new NetworkPanel();
       panel.setupDialog(null);

       ADialog temp = new ADialog(this, "Select a network", panel, null);       
       temp.pack();

       String network = (String)temp.showDialog(null);

       if (network != null && !data.isValue(network))
       {
          data.add(network);
          refresh();
          networks.setSelectedIndex(networks.getItemCount() - 1);
       }
   }

   protected static class NetworkPanel extends APanel
   {
      private JList  list;

      public void setupDialog(Object value)
      {
         DefaultListModel model = new DefaultListModel();

         Iterator i = ServerData.getServerData().getGroups().iterator();
         i.next(); // skip "All Servers"
         i.next(); // skip "Random Servers"

         while (i.hasNext())
         {
            ServerGroup group = (ServerGroup)i.next();
            model.addElement(group.getName());
         }

         list = new JList(model);

         addComponent(new JLabel("Select a network:"));
         addComponent(new JScrollPane(list));
      }
 
      public void processParent(final ADialog dialog)
      {
         list.addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent ev)
            {
               if (ev.getClickCount() > 1) 
               {
                  dialog.closeAndReturn();
               }
            }
         });
      }

      public Object getValue(Object value)
      {
         return list.getSelectedValue();
      }
   }

   public void save()
   {
      if (data != null)
         data.save();
   }

   public int getEstimatedWidth()
   {
      return 0;
   }

   public void setAlignWidth(int width)
   {
   }

   public void itemStateChanged(ItemEvent ev)
   {
      if (ev.getStateChange() == ItemEvent.SELECTED)
      {
         // fire an action listener event with the action command being the "network"
         fireEvent(networks.getSelectedItem().toString());
         notifyParent();
      }
   }

   public JComponent getComponent()
   {
      return this;
   }

   public void refresh()
   {
      networks.removeAllItems();
      networks.addItem(ALL_NETWORKS);
      
      if (data == null)
      {
         data = ClientState.getClientState().getStringList(networkV);
      }

      LinkedList temp = data.getList();
      Iterator i = temp.iterator();
      while (i.hasNext())
      {
         networks.addItem(i.next().toString());
      }

      networks.setSelectedIndex(0);
   }

   protected LinkedList listeners = new LinkedList();

   protected void fireEvent(String command)
   {
      ActionEvent ev = new ActionEvent(this, 0, command);

      Iterator i = listeners.iterator();
      while (i.hasNext())
      {
         ActionListener temp = (ActionListener)i.next();
         temp.actionPerformed(ev);
      }
   }

   public void addActionListener(ActionListener l) { listeners.add(l); }
   public void addDeleteListener(ActionListener l) { delete.addActionListener(l); }
}


