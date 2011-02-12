package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.*;
import rero.config.*;

import rero.dck.items.*;
import rero.dck.*;

public class NotifyDialog extends DMain
{
   public String getTitle()
   {
      return "Notify Setup";
   }

   public String getDescription()
   {
      return "Notify List Setup";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();
      addLabel("The following users are on your notify list:", 30);
      addBlankSpace();
      addListInput("notify.users", "Add Notify User", "User to add to notify list?", 80, 125);
   }
}



