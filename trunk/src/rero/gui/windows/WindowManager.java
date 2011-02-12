package rero.gui.windows;

import javax.swing.*;
import java.util.*;
import rero.config.*;
import java.awt.*;
import java.awt.event.*;

public abstract class WindowManager extends JPanel implements ClientStateListener, ActionListener
{
    protected SwitchBarOptions switchOptions; // we have to keep a reference to it or it will go bye bye
    protected JToolBar         switchbar;
    protected LinkedList       windows;
    protected HashMap          windowMap; /** key=<JInternalFrame> value=<StatusWindow> or some child thereof */

    protected boolean          isRelative; // is the bg being drawn relative to the main window, makes a difference in repainting..

    public StatusWindow getWindowFor(Object o)
    {
       return (StatusWindow)windowMap.get(o);
    }

    public void addToSwitchbar(StatusWindow window)
    {
       if (ClientState.getClientState().isOption("switchbar.sort", ClientDefaults.switchbar_sort))
       {
          Iterator i   = windows.iterator();
          int      pos = 0;
          while (i.hasNext())
          {
             StatusWindow temp = (StatusWindow) i.next();
             if (window.compareTo(temp) < 0)
             {
                switchbar.add(window.getButton(), pos);
                windows.add(pos, window);
                switchbar.revalidate();
                return;
             }

             pos++;
          }
       }

       windows.add(window);
       switchbar.add(window.getButton());
       switchbar.revalidate();
    }

    public void actionPerformed(ActionEvent e)
    {
       JToggleButton source = (JToggleButton)e.getSource();

       if (source.isSelected())
       {
          doActivate(getWindowFor(e.getSource()));
       }
       else
       {
          doDeactivate(getWindowFor(e.getSource()));

          int index = windows.indexOf(getWindowFor(e.getSource()));
          newActive(index, false);
       }
    }

    // find next non-minimized window to the left of the specified index without making the specified index active again
    public void newActive(int index, boolean includeCurrent)
    {
       for (int x = index - 1; x >= 0; x--)
       {
          StatusWindow temp   = (StatusWindow)windows.get(x);

          if (temp != null && !temp.getWindow().isIcon())
          {
             doActivate(temp);
             return;
          }
       }
 
       if (includeCurrent) { index = -1; }

       for (int x = windows.size() - 1; x > index; x--)
       {
          StatusWindow temp   = (StatusWindow)windows.get(x);

          if (temp != null && !temp.getWindow().isIcon())
          {
             doActivate(temp);
             return;
          }
       }
    }

    public void propertyChanged(String key, String value)
    {
       isRelative = ClientState.getClientState().isOption("window.relative", false);

       if (ClientState.getClientState().isOption("switchbar.sort", ClientDefaults.switchbar_sort))
       {
          switchbar.removeAll();

          Collections.sort(windows);

          Iterator i = windows.iterator();
          while (i.hasNext())
          {
             StatusWindow window = (StatusWindow)i.next();
             switchbar.add(window.getButton());
          }          

          switchbar.revalidate();
       }
    }

    public WindowManager()
    {
       init();
       ClientState.getClientState().addClientStateListener("switchbar.sort", this);
       ClientState.getClientState().addClientStateListener("window", this);
       isRelative = ClientState.getClientState().isOption("window.relative", false);
    }

    public LinkedList getAllWindows()
    {
       return windows;
    }

    public abstract void init();
    public abstract void addWindow(StatusWindow window, boolean selected);
    public abstract StatusWindow getActiveWindow();
    protected abstract void doActivate(StatusWindow window);
    protected abstract void doDeactivate(StatusWindow window);
}  
