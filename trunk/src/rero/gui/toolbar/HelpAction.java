package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;

public class HelpAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      SessionManager.getGlobalCapabilities().showHelpDialog("Help");
   }

   public String getDescription()
   {
      return "View jIRCii Help";
   }

    public int getIndex()
    {
       return 35;
    }
}
