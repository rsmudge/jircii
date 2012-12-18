package rero.test;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.gui.*;
import rero.gui.windows.*;

import rero.config.*;
import rero.ident.*;
import rero.util.*;

import java.net.URI;

import java.lang.reflect.*;


public class WindowTest
{
   private static void checkEnvironment()
   {
      boolean invalidenv = false;

      try
      {
         String envs = System.getProperty("java.specification.version");
         double ver  = Double.parseDouble(envs);

         if (ver == 1.4)
         {
            invalidenv = System.getProperty("java.vm.version").indexOf("1.4.1") > -1 || System.getProperty("java.vm.version").indexOf("1.4.0") > -1;
         }
         else 
         {
            invalidenv = ver < 1.4;
         }
      }
      catch (Exception ex) { invalidenv = true; }

      if (invalidenv)
      {
         String outdated = "Outdated Java Error:\njIRC Requires a java virtual machine compatible \nwith Java 1.4.2 or greater.  Download the latest \nversion of "+System.getProperty("java.vendor")+"'s Java at\n"+System.getProperty("java.vendor.url")+"\nYou are running: Java "+System.getProperty("java.version");

         System.err.println(outdated);

         Frame temp = new Frame();
         JOptionPane.showMessageDialog(temp, outdated, "Outdated Java Error", JOptionPane.ERROR_MESSAGE);
         System.exit(-1);
      }
   }

   public static void main(String args[])
   {
      checkEnvironment(); // check if user is using an old java or not, if they are kill jIRC and notify them of the truth.

      int ARGNO = 0;   

      //
      // Handle Mac OS X specific stuff (iff we are on Mac OS X... won't affect anything otherwise)
      //
//    if (System.getProperty("mrj.version") != null)
      if (ClientUtils.isMac())
      {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         System.setProperty("com.apple.macos.smallTabs",  "true");
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jIRCii");

         try 
         {
            Class   osxAdapter     = Class.forName("apple.OSXAdapter");
            Class[] defArgs        = new Class[0]; // no arguments
            Method  registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", defArgs);

            if (registerMethod != null) 
            {
               registerMethod.invoke(osxAdapter, new Object[0]);
            }
         } 
         catch (Exception ex) 
         {
            System.err.println("Exception while loading the OSXAdapter:");
            ex.printStackTrace();
         }
      }

      //
      // base directory stuff
      //
      if ((ARGNO + 1) < args.length && args[ARGNO].equals("-settings"))
      {
         ClientState.setBaseDirectory(args[ARGNO + 1]);

         ARGNO += 2;
      }

      //
      // Setup the appropriate look and feel based on user preferences and other such excitement
      //
      try
      {
         if ((ARGNO + 1) < args.length && args[ARGNO].equals("-lnf"))
         {
             UIManager.setLookAndFeel(args[ARGNO + 1]);
             ARGNO += 2;
         }
         else if (ClientState.getClientState().isOption("ui.native", ClientDefaults.ui_native))
         {       
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
         else
         {
             UIManager.LookAndFeelInfo[] feels = UIManager.getInstalledLookAndFeels();
             for (int x = 0; x < feels.length; x++)
             {
                if (feels[x].getName().equals("Nimbus")) 
                {
                   UIManager.setLookAndFeel(feels[x].getClassName());   
                }
             }
         }
      }
      catch (Exception e)
      {
         System.err.println("Could not load specified look and feel, using system default");
         e.printStackTrace();
      }

      //
      // Tell jIRCii to Watch for Proxy Server changes
      //
      ProxySettings.initialize();

      //
      // Initialize and launch the ident daemon
      //
      IdentDaemon.initialize();

      //
      // check for an irc:// specified on the command line...
      //
      if (ARGNO < args.length && (args[ARGNO].indexOf("irc://") > -1 || args[ARGNO].indexOf("ircs://") > -1))
      {
         try
         {
            new QuickConnect(new URI(args[ARGNO])); // sets up a data structure that jIRCii uses to setup auto connect for this..
         }
         catch (Exception urlex) { urlex.printStackTrace(); }

         ARGNO++;
      }

      //
      // Open the window and launch this bad boy irc client
      // 
      new WindowTest();
   }

   protected JFrame frame;

   public WindowTest()
   {
      frame = new JFrame("jIRCii");

      GlobalCapabilities.frame = frame;
  
      frame.getContentPane().setLayout(new BorderLayout());

      frame.getContentPane().add(new SessionManager(frame), BorderLayout.CENTER);

      frame.setIconImage(ClientState.getClientState().getIcon("jirc.icon", "jicon.jpg").getImage());

      int inset = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;

      frame.setBounds(ClientState.getClientState().getBounds("desktop.bounds", Toolkit.getDefaultToolkit().getScreenSize(), new Dimension(640, 480)));

      frame.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent ev)
         {
            SessionManager.getGlobalCapabilities().QuitClient();
         }
      });

      frame.addComponentListener(new ComponentAdapter()
      {
         public void componentMoved(ComponentEvent ev)
         {
            if (ClientState.getClientState().isOption("desktop.relative", false) || 
                ClientState.getClientState().isOption("window.relative", false) || 
                ClientState.getClientState().isOption("statusbar.relative", false)
               )
            {
               frame.validate();
               ClientState.getClientState().fireChange("desktop");
               ClientState.getClientState().fireChange("window");
               ClientState.getClientState().fireChange("statusbar");
               frame.repaint();
            }
         } 
      });

      frame.show();
   }
}
