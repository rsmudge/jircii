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

public class IgnoreDialog extends DMain
{
   public String getTitle()
   {
      return "Ignore Setup";
   }

   public String getDescription()
   {
      return "Ignore Mask Setup";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();
      addLabel("The following nick/host masks will be ignored:", 30);
      addBlankSpace();
      addListInput("ignore.masks", "Ignore this mask (nick!user@host):", "Add Ignore Mask", 80, 125);
   }
}



