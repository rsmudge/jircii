package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*;
import rero.config.*;

import rero.dck.*;
import rero.dck.items.*;

public class UIDialog extends DMain
{
   protected static boolean NATIVE_DIALOG_SHOWN = false;
   protected static boolean    SDI_DIALOG_SHOWN = false;
   protected static boolean    TAB_DIALOG_SHOWN = false;
   protected static boolean   MENU_DIALOG_SHOWN = false;

   public String getTitle()
   {
      return "GUI Setup";
   }

   public String getDescription()
   {
      return "User Interface Options";
   }

   private static ActionListener preferenceListener = null;

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      preferenceListener = new ActionListener()
      {
         public void actionPerformed(ActionEvent ev)
         {
            if (ClientState.getClientState().isOption("ui.showrestart", ClientDefaults.ui_showrestart))
            {
               JOptionPane.showMessageDialog((JComponent)ev.getSource(), "This change in jIRCii's interface\npreferences will not take effect\nuntil you restart jIRCii\n\nUse Alt+O to open the options\ndialog to undo this later", "Interface Setting", JOptionPane.INFORMATION_MESSAGE);
            }
         }
      };

      addDialogGroup(new DGroup("Interface Setup", 30)
      {
          public void setupDialog()
          {
             CheckboxInput a, b;

             a = addCheckboxInput("ui.native", ClientDefaults.ui_native, "Use native look and feel", 'n');
             b = addCheckboxInput("ui.sdi", ClientDefaults.ui_sdi, "Use single document interface", 'i');
             a.getCheckBox().addActionListener(preferenceListener);
             b.getCheckBox().addActionListener(preferenceListener);
          }
      });

      addBlankSpace();

      addDialogGroup(new DGroup("Interface Elements", 30)
      {
          public void setupDialog()
          {
             CheckboxInput a;

             addCheckboxInput("ui.usetoolbar", ClientDefaults.ui_usetoolbar, "Show newbie toolbar", 's');
             a = addCheckboxInput("ui.showtabs", ClientDefaults.ui_showtabs, "Show server tabs", 't');
             addCheckboxInput("ui.showbar", ClientDefaults.ui_showbar, "Show menubar", 'm');

             a.getCheckBox().addActionListener(preferenceListener);
          }
      });

      addBlankSpace();

      restartbox = addCheckboxInput("ui.showrestart", ClientDefaults.ui_showrestart,  "Show restart required warning dialog", 'r', FlowLayout.CENTER);
      restartbox.getCheckBox().addActionListener(new ActionListener() { 
         public void actionPerformed(ActionEvent ev)
         {
             restartbox.save(); // force an immediate save so this option takes effect right away
         }
      });
   }

   private CheckboxInput restartbox;
}



