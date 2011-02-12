package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.client.*;

import rero.bridges.menu.*;

public class SendAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      Capabilities client = SessionManager.getGlobalCapabilities().getActiveSession().getCapabilities();

      String nick = JOptionPane.showInputDialog(SessionManager.getGlobalCapabilities().getFrame(), "Send a file to:", "DCC Send", JOptionPane.QUESTION_MESSAGE);

      if (nick != null)
        SessionManager.getGlobalCapabilities().getActiveSession().executeCommand("/DCC send " + nick);
   }

   public String getDescription()
   {
      return "Send a file via DCC";
   } 

   public int getIndex()
   {
      return 22;
   }
}
