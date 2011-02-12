package rero.gui.sdi;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import java.beans.*;

import rero.gui.windows.*;
import rero.gui.background.*;

import rero.util.*;

import rero.config.*;

import rero.gui.toolkit.OrientedToolBar;

/** responsible for mantaining the state of the desktop GUI and switchbar */
public class ClientPanel extends WindowManager implements ActionListener, ClientStateListener
{
    protected StatusWindow active = null;

    protected JPanel     desktop, top;
    protected JLabel     button;

    public void propertyChanged(String key, String value)
    {
       if (key.equals("switchbar.position"))
       {
          int orientation = ClientState.getClientState().getInteger("switchbar.position", ClientDefaults.switchbar_position);

          top.remove(button);
          if (orientation == 2 || orientation == 3)
          {
             top.add(button, BorderLayout.SOUTH);
          }
          else
          {
             top.add(button, BorderLayout.EAST);
          }
       }
       else
       {
          super.propertyChanged(key, value);
       }
    }

    public void init()
    {
       switchbar = new OrientedToolBar();  

       top = new JPanel();
       top.setLayout(new BorderLayout(5, 0));
       
       button = new JLabel("<html><b>X</b>&nbsp;</html>", SwingConstants.CENTER);
       button.setToolTipText("Close active window");

       button.addMouseListener(new MouseAdapter()
       {
          protected Color original;

          public void mousePressed(MouseEvent e)
          {
             original = button.getForeground();
             button.setForeground(UIManager.getColor("TextArea.selectionBackground"));
          }

          public void mouseClicked(MouseEvent e)
          {
             processClose();
          }

          public void mouseReleased(MouseEvent e)
          {
             button.setForeground(original);
          }

          public void mouseEntered(MouseEvent e)
          {
             button.setText("<html><b><u>X</u></b>&nbsp;</html>");
          }

          public void mouseExited(MouseEvent e)
          {
             button.setText("<html><b>X</b>&nbsp;</html>");
          }

       });

       top.add(switchbar, BorderLayout.CENTER);

       propertyChanged("switchbar.position", null);
//       top.add(button, BorderLayout.SOUTH); // was EAST

       setLayout(new BorderLayout());

       switchOptions = new SwitchBarOptions(this, top);
 
//       add(top, BorderLayout.NORTH);

       windowMap = new HashMap();  
       windows   = new LinkedList();

       desktop = new JPanel();
       desktop.setLayout(new BorderLayout());

       add(desktop, BorderLayout.CENTER);

       new MantainActiveFocus(this);

       ClientState.getClientState().addClientStateListener("switchbar.position", this);
    }

    public void addWindow(StatusWindow window, boolean selected)
    {
       ClientSingleWindow temp = new ClientSingleWindow(this);
       window.init(temp);

       windowMap.put(window.getWindow(), window);
       windowMap.put(window.getButton(), window);

       window.getButton().addActionListener(this);

       // add to the switchbar
       addToSwitchbar(window);

       // add to the desktop
       if (selected)
       {
          if (active != null)
          {
             doDeactivate(active);
          }

          desktop.add(temp, BorderLayout.CENTER); 

          active = window;
       }

       temp.processOpen();

       if (selected)
       {
          if (!window.getButton().isSelected())
          {
             window.getButton().setSelected(true);
          }
       }

       revalidate();

       refreshFocus(); 
    }

    public void killWindow(ClientWindow cwindow)
    {
       StatusWindow window = getWindowFor(cwindow);

       if (window == null)
          return;          // making the code a little bit more robust...
       
       ((ClientSingleWindow)window.getWindow()).processClose();

       int idx = windows.indexOf(window);
 
       switchbar.remove(window.getButton());
       windowMap.remove(window.getButton());
       windowMap.remove(window.getWindow());
       windows.remove(window);

       desktop.remove(window);          

       if (window == active && active.getName().equals(StatusWindow.STATUS_NAME))
       {
          active = null; // if we close the status window, that's it... make sure we get rid fo the reference
       }
       else if (window == active)
       {
          newActive(idx, true); // if this window was the active one, make another one active instead...
          refreshFocus();
       }

       switchbar.validate();
       switchbar.repaint();
    }

    public void processClose()
    { 
       if (active != null)
       {
          killWindow(active.getWindow());
       }       
    }

    public StatusWindow getActiveWindow()
    {
       return active;
    }

    protected void doActivate(StatusWindow window)
    {
       if (active != null && active != window.getWindow())
       {
          doDeactivate(active);
       }

       desktop.add((ClientSingleWindow)window.getWindow(), BorderLayout.CENTER);

       active = window;
       ((ClientSingleWindow)active.getWindow()).processActive();

       if (!window.getButton().isSelected())
       {
          window.getButton().setSelected(true);
       }

       revalidate();
       repaint();

       refreshFocus(); 
    }

    public void refreshFocus()
    {
       SwingUtilities.invokeLater(new Runnable()
       {
          public void run()
          {
             if (getActiveWindow() != null && isShowing() && getActiveWindow().isLegalWindow() && !rero.gui.KeyBindings.is_dialog_active)
             {
                getActiveWindow().getInput().requestFocus();
             }
          }
       });
    }

    protected void doDeactivate(StatusWindow window)
    {
       desktop.remove((ClientSingleWindow)window.getWindow());
       ((ClientSingleWindow)window.getWindow()).processInactive();
       window.getButton().setSelected(false);
    }

    private class MantainActiveFocus extends ComponentAdapter
    {
        public MantainActiveFocus(JComponent component)
        {
           component.addComponentListener(this);
        }

        public void componentMoved(ComponentEvent e)
        {
        }

        public void componentShown(ComponentEvent e)
        {
           refreshFocus();
        }
    }
}
