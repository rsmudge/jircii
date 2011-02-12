package rero.gui;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.awt.image.*;

import rero.config.*;

import rero.gui.toolbar.*;
import java.util.*;

public class GraphicalToolbar extends JToolBar
{
   public static ImageIcon[] images = new ImageIcon[37];
   protected LinkedList tools = new LinkedList();

   private static GraphicalToolbar toolbar = null;
   private static StateListener    state;       // we have to keep a reference or else it will get garbage collected

   public static void startup()
   {
      // set myself up... why make the other classes do all of the work, eh?

      state = new StateListener();
      state.propertyChanged("ui.usetoolbar", "");

      ClientState.getClientState().addClientStateListener("ui.usetoolbar", state);
   }

   /** this method is cheating and I should feel very guilty for doing my toolbar code like this, I'll probably pay for it later */
   public static void stateChanged()
   {
      if (toolbar != null)
         toolbar.refresh();
   }

   public void refresh()
   {
      Iterator i = tools.iterator();
      while (i.hasNext())
      {
         ((ToolButton)i.next()).displayIcon();
      }
   }

   private static class StateListener implements ClientStateListener
   {
      public void propertyChanged(String property, String parameter)
      {
         if (toolbar != null)
            SessionManager.getGlobalCapabilities().getFrame().getContentPane().remove(toolbar);

         if (ClientState.getClientState().isOption("ui.usetoolbar", ClientDefaults.ui_usetoolbar))
         {
            if (toolbar == null)
               toolbar = new GraphicalToolbar();

            SessionManager.getGlobalCapabilities().getFrame().getContentPane().add(toolbar, BorderLayout.NORTH);
         }

        if (toolbar != null)
          SessionManager.getGlobalCapabilities().getFrame().getContentPane().validate();
      }
   }

   public GraphicalToolbar()
   {
      int[]        tiles;
      ToolAction[] actions;

      if (ClientState.getClientState().isOption("ui.sdi", ClientDefaults.ui_sdi))
      {
         tiles = new int[] { 0, -1, 4, 7, -1, 10, -1, 22, 23, 24, -1, 26, 28, -1, 35, 36 };
         actions = new ToolAction[] {
            new ConnectAction(),
            null,
            new OptionsAction(),
            new ScriptAction(), // popup menus aka script manager
            null,
            new ListAction(), // list action
            null,
            new SendAction(), // dcc send
            new ChatAction(), // dcc chat
            new DCCAction(), // dcc options
            null, 
            new NotifyAction(), // notify list options
            new NotifyAction2(), // notify list options
            null,
            new HelpAction(),
            new AboutAction(),
            new EvilAction()
         };
      }
      else
      {
         tiles = new int[] { 0, -1, 4, 7, -1, 10, -1, 22, 23, 24, -1, 26, 28, -1, 31, 32, -1, 35, 36 };
         actions = new ToolAction[] {
            new ConnectAction(),
            null,
            new OptionsAction(),
            new ScriptAction(), // popup menus aka script manager
            null,
            new ListAction(), // list action
            null,
            new SendAction(), // dcc send
            new ChatAction(), // dcc chat
            new DCCAction(), // dcc options
            null, 
            new NotifyAction(), // notify list options
            new NotifyAction2(), // notify list options
            null,
            new CascadeAction(),
            new TileAction(),
            null,
            new HelpAction(),
            new AboutAction(),
            new EvilAction()
         };
      }

      BufferedImage all = LoadToolbarImage();

      for (int x = 0; x < 37; x++)
      {
         images[x] = new ImageIcon(all.getSubimage(x * 16, 0, 16, 16));
      }

      for (int x = 0; x < actions.length; x++)
      {
         if (actions[x] == null)
         {
            addSeparator(new Dimension(12, 12));
         }
         else
         {
            ToolButton temp = new ToolButton(actions[x]);
            add(temp);
            tools.add(temp);
         }
      }

      setFloatable(false);
      setBorderPainted(true);
      setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
   }
   
   private static class ToolButton extends JLabel
   {
      private ImageIcon       normal;
      private ImageIcon       pressed;
      private ToolAction action;

      public ToolButton(ToolAction taction)
      {
         setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

         addMouseListener(new Listeners());

         action = taction;
         displayIcon();
      }

      public void displayIcon()
      {
         normal  = images[action.getIndex()];
         pressed = new ImageIcon(GrayFilter.createDisabledImage(normal.getImage()));
         setToolTipText(action.getDescription());

         setIcon(normal);
      }

      private class Listeners extends MouseAdapter
      {
         public void mouseClicked(MouseEvent ev)
         {
            action.actionPerformed(ev);
         }

         public void mousePressed(MouseEvent ev)
         {
            setIcon(pressed);
         }

         public void mouseReleased(MouseEvent ev)
         {
            setIcon(normal);
         }
      }
   }

   private static BufferedImage LoadToolbarImage()
   {
      ImageIcon original = ClientState.getClientState().getIcon("jirc.toolbar", "toolbar.gif"); 
     
      Image image = original.getImage();
      BufferedImage value = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

      Graphics g = value.getGraphics();
      g.drawImage(image, 0, 0, null);

      original.getImage().flush();
      
      return value;
   }
}
