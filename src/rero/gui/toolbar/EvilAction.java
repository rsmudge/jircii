package rero.gui.toolbar;

import rero.gui.*;

import java.awt.event.*;
import javax.swing.*;
import rero.client.*;
import rero.client.script.*;
import rero.config.*;

public class EvilAction implements ToolAction
{
   public void actionPerformed(MouseEvent ev)
   {
      if (ev.getClickCount() > 2)
      {
         boolean   lame = !ClientState.getClientState().isOption("load.lame", false);
         String message = "";

         if (lame)
         {
            message = "Hunting for easter eggs?\nRight click on a nick (in the nicklist) and\nlook for an extra surprise.";
            ((ScriptManager)SessionManager.getGlobalCapabilities().getActiveSession().getCapabilities().getDataStructure(DataStructures.ScriptManager)).loadLameScripts();
         } 
         else
         {
            message = "Ok, ok, that feature is not all it's cracked up\nto be.  Restart jIRCii to disable the lame menus";
         }

         JOptionPane.showMessageDialog(null, message, "Your favorite holiday...", JOptionPane.INFORMATION_MESSAGE);
         ClientState.getClientState().setOption("load.lame", lame);
      }
   }

   public String getDescription()
   {
      return null;
   }

   public int getIndex()
   {
      return 2;
   }
}
