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

public class WindowsDialog extends DMain
{
   public String getTitle()
   {
      return "Window UI";
   }

   public String getDescription()
   {
      return "Window User Interface Options";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      DGroup temp2 = addDialogGroup(new DGroup("Names Listbox Options", 30)
      {
          public void setupDialog()
          {
             addSelectInput("listbox.position", 1, new String[] { "Left", "Right" }, "Position:  ", 'P', 25);
             addStringInput("listbox.width",       ClientDefaults.listbox_width+"" , "Nickname Size:  ", 'N',  100); 
          }
      });

      CheckboxInput boxed2 = addCheckboxInput("listbox.enabled", true,  "Enable Channel Names Listbox", 'C', FlowLayout.CENTER);
      boxed2.addDependent(temp2);

      addBlankSpace();

      addCheckboxInput("ui.showsbar", true, "Show statusbar in windows", 'S', FlowLayout.CENTER);

      addBlankSpace();

      addColorInput("ui.editcolor", ClientDefaults.ui_editcolor, "Editbox text color", 'E');

      addBlankSpace();
   }    
}



