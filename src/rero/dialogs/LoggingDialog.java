package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.net.*;

import rero.dck.items.*;
import rero.config.*;
import rero.dck.*;

import rero.client.dcc.*;

public class LoggingDialog extends DMain
{
   public String getTitle()
   {
      return "Setup Logs";
   }

   public String getDescription()
   {
      return "Message Logging Setup";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      DGroup temp = addDialogGroup(new DGroup("Logging Options", 15)
      {
          public void setupDialog()
          {


      addBlankSpace();
      DItem tempc = addDirectoryInput("log.saveto", ClientDefaults.log_saveto, "Log Directory: ", 'D', 10);

      addBlankSpace();

      DItem tempa = addCheckboxInput("log.strip" ,    ClientDefaults.log_strip, "Strip colors from text", 'S', FlowLayout.LEFT);
      DItem tempb = addCheckboxInput("log.timestamp", ClientDefaults.log_timestamp, "Timestamp logged messages", 'T', FlowLayout.LEFT);
      addBlankSpace();
            }
      });

      addBlankSpace();

      CheckboxInput boxed = addCheckboxInput("log.enabled", ClientDefaults.log_enabled,  "Enable Logging", 'E', FlowLayout.CENTER);
      boxed.addDependent(temp);

      addBlankSpace();
   }
}
