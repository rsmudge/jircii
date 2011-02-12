package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.dck.items.*;
import rero.config.*;
import rero.dck.*;

public class SwitchBarDialog extends DMain
{
   public String getTitle()
   {
      return "Switchbar";
   }

   public String getDescription()
   {
      return "Switchbar Options";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      DGroup temp = addDialogGroup(new DGroup("Switchbar Options", 30)
      {
          public void setupDialog()
          {
              addSelectInput("switchbar.position", 0, new String[] { "Top", "Bottom", "Left", "Right" }, "Position:  ", 'P', 25);
//              addSelectInput("switchbar.position", 0, new String[] { "Top", "Bottom" }, "Position:  ", 'P', 25);
              addColorInput("switchbar.color", ClientDefaults.switchbar_color, "Activity Color", 'A');
              addCheckboxInput("switchbar.fixed", ClientDefaults.switchbar_fixed,  "Fixed width switchbar buttons", 'F', FlowLayout.LEFT);
              addCheckboxInput("switchbar.sort", ClientDefaults.switchbar_sort,  "Sort buttons alphabetically", 'F', FlowLayout.LEFT);
          }
      });

      CheckboxInput boxed = addCheckboxInput("switchbar.enabled", true,  "Enable Switchbar", 'S', FlowLayout.CENTER);
      boxed.addDependent(temp);
   }
}



