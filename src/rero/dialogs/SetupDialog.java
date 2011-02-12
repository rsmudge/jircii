package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.dck.items.*;
import rero.config.*;
import rero.dck.*;

import rero.dialogs.server.*;

public class SetupDialog extends DMain
{
   protected ServerData data = ServerData.getServerData();
   protected DItem      itema, itemb;

   public String getTitle()
   {
      return "jIRCii Setup";
   }

   public String getDescription()
   {
      return "Setup jIRCii";
   }

   public JComponent getDialog()
   {
      JPanel dialog = new JPanel();

      setupLayout(dialog);
      setupDialog();

      dialog.add(itema.getComponent(), BorderLayout.CENTER);
      dialog.add(itemb.getComponent(), BorderLayout.SOUTH);

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
      itema = addOther(new ServerList(data, 0, 150, getCapabilities()));

      itemb = addDialogGroup(new DGroup("User Information", 0)
      {
          public void setupDialog()
          {
             addStringInput("user.rname"  , "", " Real Name:  ", 'R',  10);
             addStringInput("user.email"  , "", " Email:   "   , 'E',  60);
             addStringInput("user.nick"   , "", " Nickname:   ", 'N',  60);
             addStringInput("user.altnick", "", " Alt. Nick:  ", 'A',  60);
          }
      });
   }
}



