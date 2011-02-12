package rero.gui.windows;

import rero.gui.*;
import rero.gui.input.*;
import rero.gui.background.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import rero.client.*;
import java.util.HashMap;
import rero.util.ClientUtils;

import rero.gui.toolkit.OrientedToolBar;
import rero.ircfw.*;

import rero.bridges.menu.*;

import text.*;
import rero.config.*;

public class StatusWindow extends BackgroundPanel implements IRCAwareComponent, Comparable
{
   public static final String STATUS_NAME = "%STATUS%"; // a constant representing the name of the status window as far as jIRCii goes...

   protected WrappedDisplay  display;
   protected InputField      input;
   protected WindowStatusBar statusbar;

   protected JToggleButton   button;    // for the switchbar homez.
   protected ImageIcon       icon;      // jEAH bABY

   protected ClientWindow    frame;
   protected String          query = "";

   protected Capabilities    capabilities;

   protected Color           defaultForegroundColor;

   protected MenuBridge      menuManager;

   public void cleanup()
   {
      if (display != null)
        display.clear();

      if (input != null)
      {
         input.cleanup();
      }

      removeAll();
   }

   public void touch()
   {
      statusbar.rehash();
      statusbar.repaint();
   }

   protected void maybeShowPopup(MouseEvent ev, String desc)
   {
      if (ev.isPopupTrigger())
      {
         JPopupMenu menu = getPopupMenu(desc, ClientUtils.getEventHashMap(getName(), getName()));

         if (menu != null)
         {
            menu.show((JComponent)ev.getComponent(), ev.getX(), ev.getY());
            ev.consume();
         }
      }
   }

   public void installCapabilities(Capabilities c)
   {
      capabilities = c;
      statusbar.installCapabilities(c);

      menuManager = (MenuBridge)c.getDataStructure("menuBridge");

      input.addMouseListener(new MouseAdapter()
      {
          public void mousePressed(MouseEvent ev)
          {
             maybeShowPopup(ev, "input");
          }

          public void mouseReleased(MouseEvent ev)
          {
             maybeShowPopup(ev, "input");
          }

          public void mouseClicked(MouseEvent ev)
          {
             maybeShowPopup(ev, "input");
          }
      });

      MouseAdapter normal = new MouseAdapter()
      {
          public void mousePressed(MouseEvent ev)
          {
             maybeShowPopup(ev, getWindowType());
          }

          public void mouseReleased(MouseEvent ev)
          {
             maybeShowPopup(ev, getWindowType());
          }

          public void mouseClicked(MouseEvent ev)
          {
             maybeShowPopup(ev, getWindowType());
          }
      };

      display.addMouseListener(normal);
   }

   public JPopupMenu getPopupMenu(String name, HashMap event)
   {
      return menuManager.getPopupMenu(name, event);
   }

   public InputField getInput()
   {
      return input;
   }

   public WrappedDisplay getDisplay()
   {
      return display;
   }

   public WindowStatusBar getStatusBar()
   {
      return statusbar;
   }

   public void flag()
   {
      if (!getWindow().isSelected() && SwitchBarOptions.isHilightOn())
      {
          getButton().setForeground(SwitchBarOptions.getHighlightColor());
      }
   }

   public void unflag()
   {
      if (SwitchBarOptions.isHilightOn())
      {
         getButton().setForeground(defaultForegroundColor);
         getButton().repaint();
      }
   }


   public void init(ClientWindow _frame)
   {
      frame = _frame;
      frame.addWindowListener(new ClientWindowStuff());

      setLayout(new BorderLayout());

      display   = new WrappedDisplay();
      input     = new InputField();
      statusbar = new WindowStatusBar(this);

      add(display, BorderLayout.CENTER);

      JPanel space = new JPanel();
      space.setLayout(new BorderLayout());

      space.add(statusbar, BorderLayout.NORTH);
      space.add(input,     BorderLayout.SOUTH);

      space.setOpaque(false);

      add(space, BorderLayout.SOUTH);

      frame.setContentPane(this);

      setTitle(getName());
      frame.setIcon(getImageIcon());
   }

   public String getQuery()
   {
      return query;
   }

   public void setQuery(String q)
   {
      query = q;
      statusbar.stateChanged(null); // I know that the statusbar doesn't do anything with the 'state' variable
   }

   public void setTitle(String title)
   {
      if (title != null && title.equals("%STATUS%"))
      {
         title = "Status";
      }

      frame.setTitle(title);
   }

   public ClientWindow getWindow()
   {
      return frame;
   }

   public String getTitle()
   {
      return frame.getTitle();
   }

   public void setName(String name)
   {
      getButton().setText(getName());
      getButton().repaint();
      setTitle(getName());
      touch();
   }

   public String getName()
   {
      return "%STATUS%";
   }

   public ImageIcon getImageIcon()
   {
      if (icon == null)
      {
         icon = new ImageIcon(ClientState.getClientState().getResource("status.gif"));
      }

      return icon;
   }

   public JToggleButton getButton()
   {
      if (button == null)
      {
         if (getName().equals("%STATUS%"))
         {
            button = new JToggleButton("Status", getImageIcon());
         }
         else
         {
            button = new JToggleButton(getName(), getImageIcon());
         }

         button.setHorizontalAlignment(JToggleButton.LEFT);
         button.setMargin(new Insets(0, 0, 0, 5));
         button.setFocusPainted(false);

         button.setPreferredSize(new Dimension(OrientedToolBar.BUTTON_FIXED_WIDTH, button.getPreferredSize().height));

         button.setSelected(false);

         defaultForegroundColor = button.getForeground();

         button.addMouseListener(new MouseAdapter()
         {
             public void mousePressed(MouseEvent ev)
             {
                 // Shift+Click closes windows
                 int onmask = KeyEvent.SHIFT_DOWN_MASK | KeyEvent.BUTTON1_DOWN_MASK;
                 if ((ev.getModifiersEx() & onmask) == onmask)
                 {
                     String text = ((JToggleButton) ev.getSource()).getText();
                     IRCSession session = capabilities.getGlobalCapabilities().getSessionManager().getActiveSession();
                     session.getWindow(text).getWindow().closeWindow();
                 } else {
                     maybeShowPopup(ev, "switchbar");
                 }
             }

             public void mouseReleased(MouseEvent ev)
             {
                maybeShowPopup(ev, "switchbar");
             }

             public void mouseClicked(MouseEvent ev)
             {
                maybeShowPopup(ev, "switchbar");
             }
         });
      }

      return button;
   }

   protected class ClientWindowStuff implements ClientWindowListener
   {
      public void onActive(ClientWindowEvent ev)
      {
         unflag();
      }
      public void onOpen(ClientWindowEvent ev) { }
      public void onInactive(ClientWindowEvent ev) { }
      public void onMinimize(ClientWindowEvent ev) { }
      public void onClose(ClientWindowEvent ev)
      {
         cleanup();
      }
   }

   public String getWindowType()
   {
      return "Status";
   }

   public int compareTo(Object o)
   {
      StatusWindow temp = (StatusWindow)o;

      if (compareWindowType() == temp.compareWindowType())
      {
         return this.getName().toUpperCase().compareTo(((StatusWindow)o).getName().toUpperCase());
      }

      return compareWindowType() - temp.compareWindowType();
   }

   public int compareWindowType()
   {
      return 1;
   }

   public boolean isLegalWindow()
   {
      return true;
   }

/*   protected void finalize()
   {
      System.out.println("Finalizing " + getWindowType() + ", " + getName());
   } */
}
