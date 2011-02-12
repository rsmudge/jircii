package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*;
import rero.config.*;

import java.io.*;
import rero.util.*;

import rero.dck.items.*;
import rero.dck.*;

import rero.gui.*;

public class ThemeDialog extends DMain implements ActionListener
{
   private JLabel  label;
   private JButton importt, exportt;

   public String getTitle()
   {
      return "Theme Manager";
   }

   public String getDescription()
   {
      return "Theme Manager";
   }

   public void actionPerformed(ActionEvent ev)
   {
      File file = null;

      if (ev.getSource() == importt)
      {
         file = DialogUtilities.showFileDialog("Import Theme File", null, null);
         if (file != null)
         {
            SessionManager.getGlobalCapabilities().getActiveSession().executeCommand("/theme " + file.getAbsolutePath());
         }
      }
      else if (ev.getSource() == exportt)
      {
         String name = ClientUtils.generateThemeScript(null);

         if (name != null)
         {
            ClientState.getClientState().setString("current.theme", name);
         }
      }
   }

   public void setupDialog()
   {
      label  = new JLabel();
      refresh();

      importt = new JButton("Import Theme");
      importt.setMnemonic('I');
      importt.setToolTipText("Import colormap settings from a jIRCii theme script");
      importt.addActionListener(this);

      exportt = new JButton("Export Theme");
      exportt.setMnemonic('E');
      exportt.setToolTipText("Export colormap settings to a jIRCii theme script");
      exportt.addActionListener(this);

      JPanel a = new JPanel();
      a.setLayout(new FlowLayout(FlowLayout.LEFT));
      a.add(importt);

      JPanel b = new JPanel();
      b.setLayout(new FlowLayout(FlowLayout.LEFT));
      b.add(exportt);
 
      addBlankSpace();
      addBlankSpace();
      addBlankSpace();
      addBlankSpace();
      addComponent(label);
      addBlankSpace();
      addComponent(a);
      addComponent(b);
      addBlankSpace();
      addLabel("jIRCii themes consist of colormap and background color settings.  To edit the current colormap simply hold down shift and click on text within the client.", 30);
      addBlankSpace();
      addBlankSpace();
   }
 
   public void refresh()
   {
      label.setText("Current Theme: " + ClientState.getClientState().getString("current.theme", ClientDefaults.current_theme));
   }
}



