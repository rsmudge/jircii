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

public class ScriptDialog extends DMain
{
   public String getTitle()
   {
      return "Script Manager";
   }

   public String getDescription()
   {
      return "Script Manager";
   }

   public void setupDialog()
   {
      addLabel("The following scripts are currently loaded:", 30);
      addBlankSpace();
      addOther(new FileListInput("script.files", "Select Script", "Load Script", 'L', "Unload Script", 'U', 80, 125));
      addCheckboxInput("script.ignoreWarnings", ClientDefaults.script_ignoreWarnings, "Ignore script warnings", 'I', FlowLayout.LEFT);
      addCheckboxInput("script.verboseLoad", ClientDefaults.script_verboseLoad, "Verbose script loading/unloading", 'V', FlowLayout.LEFT);
   }
}



