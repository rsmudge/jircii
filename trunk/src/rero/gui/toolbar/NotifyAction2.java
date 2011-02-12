package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;

public class NotifyAction2 implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      SessionManager.getGlobalCapabilities().showOptionDialog("Notify Setup");
   }

   public String getDescription()
   {
      return "Edit Notify List";
   }

   public int getIndex()
   {
      return 26;
   }
}
