package rero.bridges.menu;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import sleep.runtime.*;
import sleep.engine.*;

import java.util.*;

import rero.gui.*;

import rero.client.user.*;

public class SimpleItem extends JMenuItem implements ActionListener
{
   protected ScriptInstance owner;
   protected String         code;

   public SimpleItem(ScriptInstance _owner, String _label, String _code)
   {
       if (_label.indexOf('&') > -1)
       {
          setText( _label.substring(0, _label.indexOf('&')) + _label.substring(_label.indexOf('&') + 1, _label.length()) );
          setMnemonic(_label.charAt(_label.indexOf('&') + 1));
       }
       else
       {
          setText(_label);
       }

       owner = _owner;
       code  = _code;

       if (code.charAt(0) != '/')
       {
          code = '/' + code;
       }
 
       addActionListener(this);
   }

   public void actionPerformed(ActionEvent e) 
   { 
       ((UserHandler)SessionManager.getGlobalCapabilities().getActiveSession().getCapabilities().getDataStructure("commands")).processCommand(code);
   }
}
