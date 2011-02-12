package rero.gui.toolbar;

import rero.gui.*;
import rero.gui.mdi.*;

import java.awt.event.*;


public class TileAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      ((ClientDesktop)SessionManager.getGlobalCapabilities().getActiveSession().getDesktop()).tileWindows();
   }

   public String getDescription()
   {
      return "Tile Windows";
   }

   public int getIndex()
   {
      return 31;
   }
}
