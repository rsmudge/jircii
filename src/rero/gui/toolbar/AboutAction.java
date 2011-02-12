package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;
import javax.swing.*;
import rero.client.*;
import rero.client.script.*;
import rero.config.*;

public class AboutAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      if (ev.isShiftDown() && ev.isControlDown())
      {
         SessionManager.getGlobalCapabilities().showCoolAbout();
      }
      else
      {
         SessionManager.getGlobalCapabilities().showAboutDialog();
      }
   }

   public String getDescription()
   {
      return "About: jIRCii, the ultimate irc client";
   }

   public int getIndex()
   {
      return 36;
   }
}
