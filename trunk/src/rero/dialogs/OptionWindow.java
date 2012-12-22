package rero.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import rero.config.*;
import rero.dck.*;

import rero.gui.*;

import java.util.*;
import rero.util.*;

public class OptionWindow extends JDialog implements DCapabilities, TreeSelectionListener
{ 
    private static OptionWindow dialog;

    JPanel content;
    JLabel title;

    HashMap dialogs;
    DefaultMutableTreeNode items;

    public DMain current;

    public void forceSave()
    {
       if (current != null)
       {
          current.save();
          ClientState.getClientState().sync();
       }
    }

    public void refresh()
    {
       if (current != null)
          current.refresh();
    }

    public void closeDialog()
    {
       KeyBindings.is_dialog_active = false; 
       setVisible(false);
    }

    public void saveCurrent(DMain newDialog)
    {
       if (current != null)
       {
          current.save();
          ClientState.getClientState().sync();
       }

       current = newDialog;
    }

    public static void displaySpecificDialog(String name)
    {
       dialog.displayDialog(name);
    }

    public void displayDialog(String name)
    {
       if (dialogs.containsKey(name))
       {
          changeDialogs((DMain)dialogs.get(name));          
       }
    }

    public String addDialog(DMain dialog)
    {
       dialogs.put(dialog.getTitle(), dialog);
       dialog.installCapabilities(this);

       return dialog.getTitle();
    }

    public void buildTables()
    {
       dialogs = new HashMap();

       items = new DefaultMutableTreeNode("Options");
       
       DefaultMutableTreeNode category, option;

       current = new SetupDialog();

       category = new DefaultMutableTreeNode("Setup"); items.add(category);
          option = new DefaultMutableTreeNode(addDialog(current)); category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new IdentDialog())); category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new ProxyDialog())); category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new PerformDialog())); category.add(option);

       category = new DefaultMutableTreeNode("Client Options"); items.add(category);
          option = new DefaultMutableTreeNode(addDialog(new IRCOptions()));   category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new ClientOptions()));   category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new DCCOptions()));   category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new LoggingDialog()));   category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new AutoWindowDialog()));   category.add(option);

       category = new DefaultMutableTreeNode("Scripts"); items.add(category);
          option = new DefaultMutableTreeNode(addDialog(new ScriptDialog())); category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new ThemeDialog())); category.add(option);

       category = new DefaultMutableTreeNode("User Lists"); items.add(category);
          option = new DefaultMutableTreeNode(addDialog(new NotifyDialog()));   category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new IgnoreDialog()));   category.add(option);

       category = new DefaultMutableTreeNode("Interface Options"); items.add(category);
          option = new DefaultMutableTreeNode(addDialog(new UIDialog()));           category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new FontDialog()));           category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new SwitchBarDialog()));    category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new WindowsDialog()));      category.add(option);
          option = new DefaultMutableTreeNode(addDialog(new ImageDialog()));        category.add(option);
	  
	  // For the time being, enable this only if we're in OS X, since attention code doesn't work on Windows or Linux.
	  if (ClientUtils.isMac())
	  	option = new DefaultMutableTreeNode(addDialog(new AttentionDialog()));    category.add(option);
    }

    public void valueChanged(TreeSelectionEvent e) 
    {
       JTree                  theTree = (JTree)e.getSource();
       DefaultMutableTreeNode node    = (DefaultMutableTreeNode)theTree.getLastSelectedPathComponent();

       if (node == null)
          return;

       DMain temp = (DMain)dialogs.get(node.getUserObject());
       changeDialogs(temp);
    }

    private void changeDialogs(DMain temp)
    {
       if (temp == null)
          return;

       title.setText(temp.getDescription());

       saveCurrent(temp);

       content.removeAll();
       content.add(temp.getDialog(), BorderLayout.CENTER);
       temp.refresh();

       content.revalidate();
       content.repaint();
    }

    private static Frame frame;

    public static void initialize(Component comp) 
    {
       if (JOptionPane.getFrameForComponent(comp) != frame)
       {
           frame = JOptionPane.getFrameForComponent(comp);
           dialog = new OptionWindow(frame);
       }
    }

    public static String showDialog(Component comp) 
    {
        // tell the client state to save the state
        KeyBindings.is_dialog_active = true;
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);

        dialog.addWindowListener(new WindowAdapter()
        {
           public void windowClosing(WindowEvent ev) 
           { 
               KeyBindings.is_dialog_active = false; 
           }
        });

        dialog.refresh();

        return "";
    }

    private OptionWindow(Frame frame) 
    {
        super(frame, "jIRCii Options", false);

        buildTables();

        //
        //buttons
        //
        JButton closeButton = new JButton("OK");
        closeButton.setMnemonic('O');

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');

        closeButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                forceSave();
                OptionWindow.dialog.setVisible(false);
                KeyBindings.is_dialog_active = false; 
            }
        });
 
        cancelButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                // tell client state that we canceled

                OptionWindow.dialog.setVisible(false);
                dialog = null;
                OptionWindow.frame = null;
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
        JPanel general = new JPanel();
        general.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel left = new JPanel();
        left.setBorder(BorderFactory.createEtchedBorder());
        left.setPreferredSize(new Dimension(195, 295));
        left.setLayout(new BorderLayout());
        left.add(general, BorderLayout.CENTER);

	//
        // General Tab
	//
        JTree genOptions = new JTree(items);
        genOptions.setRootVisible(false);
        genOptions.setToggleClickCount(1); // 1 click to expand the tree...
        genOptions.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (int x = 0; x < genOptions.getRowCount(); x++)
           genOptions.expandPath(genOptions.getPathForRow(x));

        genOptions.addTreeSelectionListener(this);

        JScrollPane genScroller = new JScrollPane(genOptions);
        genScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        genScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        general.setLayout(new BorderLayout(5, 5));

        general.add(new JLabel("Options:"), BorderLayout.NORTH);
        general.add(genScroller, BorderLayout.CENTER);

        //
	// Center - the display pane and its doings
        //
        JPanel center = new JPanel();
	center.setLayout(new BorderLayout(5, 5));

        JPanel titlep = new JPanel();
        title = new JLabel("Configuration");
        
        titlep.add(title, BorderLayout.CENTER);
        titlep.setBorder(BorderFactory.createEtchedBorder());

        center.add(titlep, BorderLayout.NORTH);
		
        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEtchedBorder());

        center.add(content, BorderLayout.CENTER);

        content.add(current.getDialog(), BorderLayout.CENTER);

        //
	// Bottom - a Close button aligned to the right
	//
        JPanel south = new JPanel();
	south.setLayout(new BorderLayout(5, 5));

        JPanel evil = new JPanel();
        GridLayout gl = new GridLayout(1, 3);
        gl.setHgap(5);
        evil.setLayout(gl);
        evil.add(closeButton);
        evil.add(cancelButton);

	south.add(evil, BorderLayout.EAST);
        south.add(new JPanel(), BorderLayout.CENTER);

	//
	// putting it all together
	//
	main.add(left, BorderLayout.WEST);
	main.add(center, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        pack();

	setSize(new Dimension(590, 440)); // was 520x363
    }
}
