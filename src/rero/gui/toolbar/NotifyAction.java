package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.client.*;

import rero.bridges.menu.*;

public class NotifyAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      Capabilities client = SessionManager.getGlobalCapabilities().getActiveSession().getCapabilities();

      SessionManager.getGlobalCapabilities().getActiveSession().executeCommand("/NOTIFY");
   }

   public String getDescription()
   {
      return "Show notify list";
   } 

   public int getIndex()
   {
      return 27;
   }
}
