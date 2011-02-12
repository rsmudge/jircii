package text;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class FindDialog extends JDialog implements ActionListener, KeyListener
{
   protected JButton next, prev;
   protected WrappedDisplay display;
   protected JTextField     search;
   protected ListIterator   results = null;

   public void showDialog()
   {
      rero.gui.KeyBindings.is_dialog_active = true;
      results = null;
      next.setEnabled(false);
      prev.setEnabled(true);
      pack();
      setLocationRelativeTo(display);
      setVisible(true);
      this.search.selectAll();
      search.requestFocus();
   }

   public FindDialog(Component comp, String title, WrappedDisplay _display, String text)
   {
       super(JOptionPane.getFrameForComponent(comp), title, false);

       display = _display;

       getContentPane().setLayout(new BorderLayout());

       search = new JTextField(text, 20);
       search.addKeyListener(this);
       search.addActionListener(this);

       JPanel panel = new JPanel();
       panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
       panel.setLayout(new FlowLayout());
       panel.add(new JLabel("Find: "));
       panel.add(search);

       getContentPane().add(panel, BorderLayout.CENTER);

       JPanel buttons = new JPanel();
       buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));

       next = new JButton("Next");
       next.setMnemonic('N');
       next.setEnabled(false);
       next.addActionListener(this);

       prev = new JButton("Previous");
       prev.setMnemonic('P');
       prev.setEnabled(text.length() > 0);
       prev.addActionListener(this);


       buttons.add(prev);
       buttons.add(next);

       getContentPane().add(buttons, BorderLayout.SOUTH);

       addWindowListener(new WindowAdapter()
       {
          public void windowClosing(WindowEvent e)
          {
             rero.gui.KeyBindings.is_dialog_active = false;
             setVisible(false);
          }
       });
   }

   public static void main(String args[])
   {
       FindDialog temp = new FindDialog(null, "Testing", null, "hi");
       temp.showDialog();
   }

   public void keyTyped(KeyEvent ev)
   {
   }

   public void keyPressed(KeyEvent ev) { }
   public void keyReleased(KeyEvent ev)
   {
       if (ev.getKeyCode() == KeyEvent.VK_ESCAPE)
       {
          rero.gui.KeyBindings.is_dialog_active = false;
          setVisible(false);
          ev.consume();
       }
       else if (!ev.isActionKey() && ev.getKeyCode() != KeyEvent.VK_ENTER)
       {
          results = null;
          next.setEnabled(search.getText().length() > 0);
          prev.setEnabled(search.getText().length() > 0);
       }
   }

   protected Object lastSource = null;

   public void actionPerformed(ActionEvent ev)
   {
       if (results == null)
       {
          results = display.find(search.getText());
          lastSource = null;
       }

       if ((ev.getSource() == next) && results.hasNext())
       {
          if (lastSource == prev) { results.next(); }

          int gotoz = (  (Integer)results.next()  ).intValue();

          display.scrollTo(gotoz);

          lastSource = next;
       }
       else if ((ev.getSource() == prev || ev.getSource() == search) && results.hasPrevious())
       {
          if (lastSource == next) { results.previous(); }

          int gotoz = (  (Integer)results.previous()  ).intValue();

          display.scrollTo(gotoz);

          lastSource = prev;
       }

       next.setEnabled(results.hasNext());
       prev.setEnabled(results.hasPrevious());

       search.requestFocus();
   }
}
