package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.dck.items.*;
import rero.config.*;
import rero.dck.*;

public class IdentDialog extends DMain
{
   public String getTitle()
   {
      return "Identd Setup";
   }

   public String getDescription()
   {
      return "Ident Daemon Setup";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      DGroup temp = addDialogGroup(new DGroup("Ident Options", 30)
      {
          public void setupDialog()
          {
             addStringInput("ident.userid", ClientDefaults.ident_userid , "  User ID:  ", 'u',  60);
             addStringInput("ident.system", ClientDefaults.ident_system , "  System:   ", 's',  60);
             addStringInput("ident.port"  , ClientDefaults.ident_port+"",   "  Port:     ", 'p', 120);
          }
      });

      CheckboxInput boxed = addCheckboxInput("ident.enabled", ClientDefaults.ident_enabled,  "Enable Ident Server", 'E', FlowLayout.CENTER);
      boxed.addDependent(temp);
   }
}



