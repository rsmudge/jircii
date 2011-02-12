package rero.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.dck.items.*;
import rero.config.*;
import rero.dck.*;

public class ProxyDialog extends DMain
{
   public String getTitle()
   {
      return "Proxy Setup";
   }

   public String getDescription()
   {
      return "SOCKS Proxy Settings";
   }

   public void setupDialog()
   {
      addBlankSpace();
      addBlankSpace();

      DGroup temp = addDialogGroup(new DGroup("SOCKS Proxy Settings", 30)
      {
          public void setupDialog()
          {
             addStringInput("proxy.server", ClientDefaults.proxy_server, "  Hostname: ", 'h',  60);
             addStringInput("proxy.port"  , ClientDefaults.proxy_port, "  Port:     ", 'p', 120);
             addBlankSpace();
             addStringInput("proxy.userid", ClientDefaults.proxy_userid, "  Username: ", 'u',  60);
             addStringInput("proxy.password", ClientDefaults.proxy_password, "  Password: ", 's',  60);
          }
      });

      CheckboxInput boxed = addCheckboxInput("proxy.enabled", false,  "Use Proxy Server", 'P', FlowLayout.CENTER);
      boxed.addDependent(temp);
   }
}



