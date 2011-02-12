package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;

public class OptionsAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      SessionManager.getGlobalCapabilities().showOptionDialog("");
   }

   public String getDescription()
   {
      return "View and edit options";
   }

   public int getIndex()
   {
      return 4;
   }
}
