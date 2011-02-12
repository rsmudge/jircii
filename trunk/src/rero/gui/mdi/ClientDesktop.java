package rero.gui.mdi;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import java.beans.*;

import rero.gui.windows.*;
import rero.gui.background.*;

import rero.util.ClientUtils;
import rero.config.*;

import rero.gui.toolkit.OrientedToolBar;

/** responsible for mantaining the state of the desktop GUI and switchbar */
public class ClientDesktop extends WindowManager implements ClientWindowListener, ClientStateListener
{
    protected JDesktopPane desktop;

    public void init()
    {
       desktop   = new JDesktopPane();
       switchbar = new OrientedToolBar();  

       setLayout(new BorderLayout());
       add(desktop, BorderLayout.CENTER);

       switchOptions = new SwitchBarOptions(this, switchbar);
//       add(switchbar, BorderLayout.NORTH);

       windowMap = new HashMap();  
       windows   = new LinkedList();

       desktop.setDesktopManager(new MyModifiedDesktopManager());

       MantainActiveFocus temp = new MantainActiveFocus(this); // object automagically registers itself as a listener.

       BackgroundDesktop wallpaper = new BackgroundDesktop(desktop);
       wallpaper.setSize(Toolkit.getDefaultToolkit().getScreenSize());
       desktop.add(wallpaper, new Integer(Integer.MIN_VALUE));
    }

    public void addWindow(StatusWindow window, boolean selected)
    {
       ClientInternalWindow temp = new ClientInternalWindow();
       window.init(temp);

       Rectangle bounds = ClientState.getClientState().getBounds(window.getWindowType()+".size", desktop.getSize(), new Dimension(480, 300));

       temp.setBounds(bounds);

       windowMap.put(window.getWindow(), window);
       windowMap.put(window.getButton(), window);

       window.getWindow().addWindowListener(this);
       window.getButton().addActionListener(this);

       // add to the switchbar
       addToSwitchbar(window);

       // add to the desktop
       desktop.add((JInternalFrame)window.getWindow());

       if (!selected)
       {
          temp.setVisible(false);
       }
       else
       {
          window.getWindow().show();
       }

       // one of my hacks...  make sure the selected window knows it is the selected window.
       // for some reason when I first launch the program this concept doesn't take so well.
 
       ClientUtils.invokeLater(new Runnable()
       {
          public void run()
          {
              if (desktop.getSelectedFrame() != null && !desktop.getSelectedFrame().isSelected())
              {
                 try
                 {
                    refreshFocus(desktop.getSelectedFrame()); 
                    desktop.getSelectedFrame().setSelected(true);
                 }
                 catch (java.beans.PropertyVetoException ex) { }
                 desktop.repaint();
              }
          }
       });
    }

    public void onActive(ClientWindowEvent ev) 
    { 
       StatusWindow temp = getWindowFor(ev.getSource());
       doActivate(temp);
    }

    public void onInactive(ClientWindowEvent ev) 
    { 
       doDeactivate(getWindowFor(ev.getSource()));
    }

    public void onMinimize(ClientWindowEvent ev) 
    { 
       boolean wasSelected = ev.getSource().isSelected();
       doDeactivate(getWindowFor(ev.getSource()));

       if (wasSelected)
       {
          int index = windows.indexOf(getWindowFor(ev.getSource()));
          newActive(index, false);
          refreshFocus(desktop.getSelectedFrame()); 
       }
    }

    public void onOpen(ClientWindowEvent ev) 
    { 
       StatusWindow temp = getWindowFor(ev.getSource());

       try
       {
          /* check if current window is maxed, if it is, maximize the new one */
          JInternalFrame f = desktop.getSelectedFrame();
          if (f != null && f.isMaximum())
          {
              doActivate(temp);
              ((JInternalFrame)ev.getSource()).setMaximum(true);
          }
          else
          {
              doActivate(temp);
          }
       }
       catch (Exception ex) { ex.printStackTrace(); }
    }

    public void onClose(ClientWindowEvent ev) 
    { 
       int index = windows.indexOf(getWindowFor(ev.getSource()));
       boolean wasSelected = ev.getSource().isSelected();

       ClientWindow window = ev.getSource();
       StatusWindow temp = (StatusWindow)windowMap.get(window);

       saveBounds(temp);

       switchbar.remove(temp.getButton());

       windowMap.remove(window);
       windowMap.remove(temp.getButton());
       windowMap.remove(temp.getWindow());
       windows.remove(temp);

       switchbar.validate();
       switchbar.repaint();

       if (desktop.getSelectedFrame() == null)
       {
          newActive(index, true);
          refreshFocus(desktop.getSelectedFrame()); 
       }
    }

    public StatusWindow getActiveWindow()
    {
       JInternalFrame f = desktop.getSelectedFrame();
       return getWindowFor(f);
    }

    protected void doActivate(StatusWindow window)
    {
       try 
       {
          JInternalFrame temp = (JInternalFrame)window.getWindow();

          if (!((ClientInternalWindow)window.getWindow()).isOpen())
          {
             window.getWindow().show();
          }

          if (!temp.isSelected())
          {
             JInternalFrame[] ftemp = desktop.getAllFrames();

             for (int x = 0; x < ftemp.length; x++)
             {
                if (ftemp[x].isSelected()) { ftemp[x].setSelected(false); }
             }
          }    

          if (temp.isIcon())
          {
             temp.setIcon(false);
          }

          desktop.setSelectedFrame(temp);
          window.getButton().setSelected(true);

          temp.setSelected(true);

          if (window.isLegalWindow())
            window.getInput().requestFocus();

          saveBounds(window);
       } 
       catch (PropertyVetoException ex) { ex.printStackTrace(); }
    }

    private void saveBounds(StatusWindow window)
    {
       JInternalFrame temp = (JInternalFrame)window.getWindow();

       if (window.getWindow().isMaximum())
       {
          ClientState.getClientState().setBounds(window.getWindowType()+".size", new Rectangle(0, 0, (int)desktop.getSize().getWidth(), (int)desktop.getSize().getHeight()));
       }
       else
       {
          ClientState.getClientState().setBounds(window.getWindowType()+".size", temp.getBounds());
       }
    }

    public void refreshFocus(JInternalFrame f)
    {
       if (f != null && isShowing() && getWindowFor(f).isLegalWindow() && !rero.gui.KeyBindings.is_dialog_active)
          getWindowFor(f).getInput().requestFocus();
    }

    protected void doDeactivate(StatusWindow window)
    {
       JInternalFrame temp = (JInternalFrame)window.getWindow();

       if (temp.isSelected())
       {
          try
          {
             temp.setIcon(true);
          }
          catch (Exception ex) { }
       }

       window.getButton().setSelected(false);

       try
       {
          temp.setSelected(false);
       }
       catch (Exception ex) { }
    }

    private int totalOpenWindows()
    {
       int wincount = 0;
       for (int x = 0; x < desktop.getAllFrames().length; x++)
       {
          JInternalFrame cwin = desktop.getAllFrames()[x];
          if (!cwin.isIcon()) { wincount++; }
       }

       return wincount;
    }

    public void cascadeWindows()
    {
       // Add your handling code here:
       Dimension size = desktop.getSize();
       int width      = size.width  * 4/5;
       int height     = size.height * 2/3;
       int total      = totalOpenWindows();

       if (total <= 0) 
           return;

       JInternalFrame[] frames = desktop.getAllFrames();

       for (int x = 0, pos = total - 1; x < frames.length; x++)
       {
          try 
          {
             if (!frames[x].isIcon())
             {
                try { frames[x].setMaximum(false); } catch (Exception ex) { }
                frames[x].setSize(width, height);
                frames[x].setLocation(pos * 20, pos * 20);
                pos--;
             }
          }
          catch (Exception ex) { }
       }
    }
 
    public void tileWindows()
    {
       JInternalFrame[] frames = desktop.getAllFrames();
       int total  = totalOpenWindows();

       if (total <= 0) 
           return;

       int numcols = (int)(Math.sqrt((double)total));
       int numrows = (total / numcols);
       if ((total % numcols) != 0) { numrows++; }

       Dimension size = desktop.getSize();
       int width  = size.width  / numcols;
       int height = size.height / numrows;

       int ypos = 0;
       int winno = 1;

       for (int z = 0; z < frames.length; z++)
       {
          if (!frames[z].isIcon())
          {
             try { frames[z].setMaximum(false); } catch (Exception ex) { }
             frames[z].setSize(new Dimension(width, height));
             frames[z].setLocation((winno - 1) * width, ypos);
             if (winno == numcols)
             {
                winno = 0;
                ypos += height;
             }
             winno++;
          }  
       }
    }

    protected class MyModifiedDesktopManager extends DefaultDesktopManager
    {
        public void closeFrame(JInternalFrame f) { }

        public void iconifyFrame(JInternalFrame f)
        { 
           boolean findNew = f.isSelected();

           f.setVisible(false);
           f.getParent().repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }

        public void deiconifyFrame(JInternalFrame f)
        {
           f.setVisible(true);
        }

        public void dragFrame(JComponent f, int newX, int newY)
        {
           super.dragFrame(f, newX, newY);

           if (isRelative)
           {
//              f.repaint();  // smart dragging feature, causes window to be repainted while it is being dragged, unfortunately
                              // it comes out jerky and looking like crap.  So I'm disabling it here.
           }
        }
 
        public void endDraggingFrame(JComponent f)
        {
           super.endDraggingFrame(f);

           if (isRelative)
           {
              f.repaint();
           }
        }
    }


    private class MantainActiveFocus extends ComponentAdapter
    {
        protected ClientDesktop desktop;

        public MantainActiveFocus(ClientDesktop mine)
        {
           desktop = mine;
           desktop.addComponentListener(this);
        }

        public void componentShown(ComponentEvent e)
        {
           JDesktopPane temp = desktop.desktop;

           if (temp.getSelectedFrame() != null)
           {
              SwingUtilities.invokeLater(new Runnable() 
              {
                 public void run()
                 {
                    try
                    {
                       if (!desktop.desktop.getSelectedFrame().isSelected())
                       {
                          desktop.desktop.getSelectedFrame().setSelected(true);
                       }
                       desktop.refreshFocus(desktop.desktop.getSelectedFrame());
                    }
                    catch (Exception ex) { ex.printStackTrace(); }
                 }
              });

              temp.repaint();
           }
        }
    }
}
