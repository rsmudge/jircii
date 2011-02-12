package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*;
import rero.config.*;

import rero.dck.items.*;
import rero.dck.*;

public class PerformDialog extends DMain implements DParent, ActionListener
{
   public String getTitle()
   {
      return "Perform";
   }

   public String getDescription()
   {
      return "Perform on Connect";
   }

   protected String current = NetworkSelect.ALL_NETWORKS;

   public void actionPerformed(ActionEvent ev)
   {
      itemc.save();
      current = ev.getActionCommand();
   }

   public void notifyParent(String variable)
   {
      ClientState.getClientState().fireChange("perform");
      itemc.refresh();          
   }

   public String getVariable(String variable)
   {
      return "perform." + current.toLowerCase();
   }

   protected DItem itemb, itemc;
   protected CheckboxInput itema;

   public JComponent getDialog()
   {
      JPanel dialog = new JPanel();

      setupLayout(dialog);
      setupDialog();

      dialog.add(itema.getComponent(), BorderLayout.SOUTH);

      dialog.add(itemb.getComponent(), BorderLayout.NORTH);
      dialog.add(itemc.getComponent(), BorderLayout.CENTER);

      return dialog;
   }

   public JComponent setupLayout(JComponent component)
   {
      component.setLayout(new BorderLayout(3, 3));
      component.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

      return component;
   }

   public void setupDialog()
   { 
      itema = addCheckboxInput("perform.enabled", false,  "Perform these commands when connecting", 'P', FlowLayout.LEFT);

      itemb = addNetworkSelector("perform.networks", "perform.cnetwork");
      itemc = addTextInput(".perform", 5); // doesn't really matter

      ((NetworkSelect)itemb).addActionListener(this);
      ((NetworkSelect)itemb).addDeleteListener((TextInput)itemc);

      itemb.setParent(this);
      itemc.setParent(this);

      itema.addDependent(itemb);
      itema.addDependent(itemc);
   }

   public void refresh()
   {
      current = NetworkSelect.ALL_NETWORKS;
      itemc.refresh();
      super.refresh();
   }
}



