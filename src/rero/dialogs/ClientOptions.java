package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import rero.config.*;
import rero.dck.*;

public class ClientOptions extends DMain
{
   public String getTitle()
   {
      return "Client Options";
   }

   public String getDescription()
   {
      return "Client Options";
   }

   public void setupDialog()
   {
      addBlankSpace();

      addCheckboxInput("update.ial" , ClientDefaults.option_showmotd,  "Update IAL on channel join", 'I');
      addCheckboxInput("option.reconnect" , ClientDefaults.option_reconnect,  "Auto-reconnect when disconnected", 'r');
   }
}



