package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*;
import rero.config.*;

import rero.dck.*;
import rero.dck.items.*;
import rero.util.*;

public class AttentionDialog extends DMain
{
   public String getTitle()
   {
      return "Notifications";
   }

   public String getDescription()
   {
      return "Activity Notification Options";
   }

   // Draw the option dialog
   // This will draw a different options list for each of the supported operating systems
   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      if (ClientUtils.isMac()) {
	      // Draw Mac OS X attention options
      	      DGroup temp2 = addDialogGroup(new DGroup("Mac OS X", 10)
      	      {
          		public void setupDialog()
         		{
				addLabelNormal("Choose one or more events on which to bounce the jIRCii dock icon:", FlowLayout.CENTER);
				CheckboxInput cbBounceDockIconMsg = addCheckboxInput("option.attention.osx.bouncedock.msg", true, "Private Message", 'P', FlowLayout.LEFT);
				CheckboxInput cbBounceDockIconNotice = addCheckboxInput("option.attention.osx.bouncedock.notice", true, "Notice", 'N', FlowLayout.LEFT);
				CheckboxInput cbBounceDockIconChannelChat = addCheckboxInput("option.attention.osx.bouncedock.channelchat", false, "Channel Chat", 'C', FlowLayout.LEFT);
				CheckboxInput cbBounceDockIconActions = addCheckboxInput("option.attention.osx.bouncedock.actions", false, "Disconnected, Killed, or Kicked from Channel", 'D', FlowLayout.LEFT);
				addBlankSpace();
				CheckboxInput cbBounceDockIconRepeat = addCheckboxInput("option.attention.osx.bouncedock.repeat", true, "Repeatedly Bounce Until Opened", 'R', FlowLayout.LEFT);
				addBlankSpace();
			}
              });
      }
      else if (ClientUtils.isWindows()) {
      }
      else if (ClientUtils.isLinux()) {
      }
   }
}
