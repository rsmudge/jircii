package rero.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;

import rero.config.*;
import rero.dialogs.help.*;
import rero.gui.*;

import rero.util.ClientUtils;

import java.net.*;

import java.util.*;

public class HelpWindow extends JDialog implements HyperlinkListener
{
    private static HelpWindow dialog;
    private static HashMap helpData = new HashMap();
    private static HelpData commandData;

    private JEditorPane display;
    private JScrollPane scroller;

    public static LinkedList getBuiltInAliases()
    {
       if (commandData == null)
           commandData = new HelpData(); 

       return commandData.getAliases();
    }

    public static HelpData getCommandData()
    {
       return commandData;
    }

    public String getCommand(String key)
    {
       return commandData.getCommand(key).toString();
    }

    public void scrollTo(String ref)
    {
       display.scrollToReference(ref);
    }

    public void updateText(String text)
    {
       display.setText(text);
       display.setCaretPosition(0);
    }

    private static Frame frame;

    public static void initialize(Component comp) 
    {
        if (JOptionPane.getFrameForComponent(comp) != frame)
        {
           frame = JOptionPane.getFrameForComponent(comp);
           dialog = new HelpWindow(frame);
	}
    }

    public static String showDialog(Component comp) 
    {
        KeyBindings.is_dialog_active = true;
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);

        dialog.addWindowListener(new WindowAdapter()
        {
           public void windowClosing(WindowEvent ev) { KeyBindings.is_dialog_active = false;  }
        });
        return "";
    }

    private HelpWindow(Frame frame) 
    {
	super(frame, "jIRCii Help", false);

        if (commandData == null)
           commandData = new HelpData();
		
        //buttons
        JButton closeButton = new JButton("Ok");
        closeButton.setMnemonic('O');

        closeButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                HelpWindow.dialog.setVisible(false);
                KeyBindings.is_dialog_active = false;
            }
        });

        getContentPane().setLayout(new BorderLayout(5, 5));
        
	JPanel main = new JPanel();
	main.setLayout(new BorderLayout(5, 5));
	main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        getContentPane().add(main, BorderLayout.CENTER);

        //
        // Left - the tabbed pane and its doings
        //
        HelpNormal   general  = new HelpNormal();
        general.setHelp(this);

        HelpCommands commands = new HelpCommands();
        commands.setHelp(this);

	JTabbedPane left = new JTabbedPane();
	left.addTab("Help", null, general.getNavigation(), "Detailed Information.");
	left.addTab("Commands", null, commands.getNavigation(), "jIRCii command reference.");

        left.setSelectedIndex(0);
        left.setPreferredSize(new Dimension(175, 295));
		
	//
	// Center - the display pane and its doings
        //
        JPanel center = new JPanel();
	center.setLayout(new BorderLayout(5, 5));
        JPanel space = new JPanel();
	space.setPreferredSize(new Dimension(0, 15));

	center.add(space, BorderLayout.NORTH);
		
   	display = new JTextPane();
        display.setEditable(false);
        display.setContentType("text/html"); // so we can do some nice formatting of the help text.
        display.setOpaque(true);
        display.setBackground(Color.white);

        display.addHyperlinkListener(this);
  
        scroller = new JScrollPane(display);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setPreferredSize(new Dimension(250, 250));

        center.add(scroller, BorderLayout.CENTER);

        //
	// Bottom - a Close button aligned to the right
	//
        JPanel south = new JPanel();
	south.setLayout(new BorderLayout(5, 5));

	south.add(closeButton, BorderLayout.EAST);
        south.add(new JPanel(), BorderLayout.CENTER);

	//
	// putting it all together
	//
	main.add(left, BorderLayout.WEST);
	main.add(center, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        pack();

	setSize(new Dimension(600, 363));
//	setSize(new Dimension(605, 363));

        general.showHelpOn("About"); 
    }

    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
              try
              {
                 if (ev.getURL().getRef() != null)
                 {
                    display.scrollToReference(ev.getURL().getRef());
                 }
                 else
                 {
                    ClientUtils.openURL(ev.getURL().toString());
                 } 
              }
              catch (Exception ex) { }
        }
    }
}
