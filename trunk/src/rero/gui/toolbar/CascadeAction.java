package rero.gui.toolbar;

import rero.gui.*;
import rero.gui.mdi.*;

import java.awt.event.*;


public class CascadeAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      ((ClientDesktop)SessionManager.getGlobalCapabilities().getActiveSession().getDesktop()).cascadeWindows();
   }

   public String getDescription()
   {
      return "Cascade Windows";
   }

   public int getIndex()
   {
      return 32;
   }
}
