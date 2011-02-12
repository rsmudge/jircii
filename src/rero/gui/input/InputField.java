package rero.gui.input;

import rero.config.ClientDefaults;
import rero.config.ClientState;
import rero.config.ClientStateListener;
import text.AttributedString;
import text.AttributedText;
import text.ModifyColorMapDialog;
import text.TextSource;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class InputField extends JTextField implements KeyListener, ActionListener, MouseListener, ClientStateListener {
  //protected InputList  list = null;
  protected Border defaultBorder;
  protected LinkedList listeners;
  protected UserInputEvent event;

  // This is the command history for this InputField
  // WARNING: Do not, under any circumstance, modify this structure.
  //          This will lead to a ConcurrentModificationException!
  private ArrayList commandHistory;

  // An iterator for the command history. This is what is actually
  // used for manipulating the command history.
  private ListIterator commandIterator;

  // This is the maximum number of commands in the command history.
  // TODO: This should be configurable.
  private int maxCommands = 30;

  // True if the key pressed in the previous keyevent was the upkey
  // This flag is modified in a variety of places in the code.
  private boolean previousKeyUpArrow = true;

  protected InputBorder indent;

  public void cleanup()
  {
      /* apparently this unholy triad of stuff is responsible for Java
         holding on to each session... */

      listeners = null;

      getParent().remove(this);

      MouseListener mickey[] = getMouseListeners();
      for (int x = 0; x < mickey.length; x++)
      {
         removeMouseListener(mickey[x]);
      }
  }

  public void mouseClicked(MouseEvent ev) {
    if (ev.getButton() == MouseEvent.BUTTON1 && ev.isShiftDown() && indent != null) {
      AttributedText temp =
        indent.getAttributes().getAttributesAt(ev.getX() - defaultBorder.getBorderInsets(this).left);

      if (temp != null) {
        if (temp.backIndex != -1 && ev.isControlDown()) {
          ModifyColorMapDialog.showModifyColorMapDialog((JComponent) ev.getSource(), temp.backIndex);
        } else {
          ModifyColorMapDialog.showModifyColorMapDialog((JComponent) ev.getSource(), temp.foreIndex);
        }
        repaint();
      }
    }
  }

  public boolean isFocusable() {
    return true;
  }

  public void mouseEntered(MouseEvent ev) {
  }

  public void mouseExited(MouseEvent ev) {
  }

  public void mousePressed(MouseEvent ev) {
  }

  public void mouseReleased(MouseEvent ev) {
  }

  public InputField() {
    setUI(new javax.swing.plaf.basic.BasicTextFieldUI());

    setOpaque(false);

    defaultBorder =
      BorderFactory.createEmptyBorder(1, TextSource.UNIVERSAL_TWEAK, 1, 1); // a 1 pixel empty border all around;
    setBorder(defaultBorder);
/*
       setBackground(null); // suggested by Sun as a fix to a background being painted problem
                            // in the GTK+ look and feel, unfortunately it doesn't work... maybe it will when 1.5 comes out
*/

    addActionListener(this);
    addKeyListener(this);

    listeners = new LinkedList();

    event = new UserInputEvent();
    event.source = this;

    indent = null;

    addMouseListener(this);

    rehashColors();

    ClientState.getClientState().addClientStateListener("ui.editcolor", this);
    ClientState.getClientState().addClientStateListener("ui.font", this);

    // Instantiate the command history -oracel
    this.commandHistory = new ArrayList(maxCommands + 1);
    this.commandIterator = commandHistory.listIterator();

  }

  public void propertyChanged(String name, String parms) {
    rehashColors();
  }

  public void rehashColors() {
    Color temp = ClientState.getClientState().getColor("ui.editcolor", ClientDefaults.ui_editcolor);

    setForeground(temp);
    setCaretColor(temp.brighter());

    setFont(ClientState.getClientState().getFont("ui.font", ClientDefaults.ui_font));

    revalidate();
  }

  public void actionPerformed(ActionEvent ev) {
    event.text = ev.getActionCommand();

    if (event.text.length() <= 0) {
      fireInputEvent(); // fire an empty input event, it helps sometimes
      return;
    }

    fireInputEvent();
  }

  public void addInputListener(InputListener l) {
    // we use addFirst for the following reasons...  generally input fields will have two listeners
    // the client itself will be listening and then there is a sort of master listener for all of the scripts.
    // the client itself will of course register the listener first
    // the master listener for scripts will register its listener second
    // by firing listeners in a last in first fired manner the scripts will get a chance to halt the processing
    // of the input event.   These kinds of things can be tough to keep track of so that is why I write this
    // comment.

    listeners.addFirst(l);
  }

  public void fireInputEvent() {
    ListIterator i = listeners.listIterator();

    // Add text to history
    this.addToHistory(event.text);

    setText(""); // clear the textbox first, on input will receive the event information 

    while (i.hasNext()) {
      InputListener temp = (InputListener) i.next();
      temp.onInput(event);
    }

    this.resetIterator(false);

    event.reset();
  }

  public String getIndent() {
    if (indent != null) {
      return indent.getText();
    }

    return "";
  }

  public void setIndent(String text) {
    if (text != null) {
      indent = new InputBorder(text);
      setBorder(new CompoundBorder(defaultBorder, indent));
    } else {
      setBorder(defaultBorder);
      indent = null;
    }
  }

  public void keyTyped(KeyEvent e) {
    //
    // deal with problem of windows binging when hitting backspace in an empty buffer
    //
    if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && getText().length() == 0) {
      e.consume();
    }
  }

  public String getCurrentText() {
    return getText();
  }


  public void keyPressed(KeyEvent e) {
    //
    // special built in control codes..
    //
    if (e.getModifiers() == 2) {
      int caretpos = getCaretPosition() + 1;

      switch (e.getKeyCode()) {
        case 75: // control-k color
          setText(getText().substring(0, getCaretPosition()) + AttributedString.color +
            getText().substring(getCaretPosition(), getText().length()));
          setCaretPosition(caretpos);
          e.consume();
          return;
        case 85: // control-u underline
          setText(getText().substring(0, getCaretPosition()) + AttributedString.underline +
            getText().substring(getCaretPosition(), getText().length()));
          setCaretPosition(caretpos);
          e.consume();
          return;
        case 66: // control-b bold
          setText(getText().substring(0, getCaretPosition()) + AttributedString.bold +
            getText().substring(getCaretPosition(), getText().length()));
          setCaretPosition(caretpos);
          e.consume();
          return;
        case 79: // control-o cancel
          setText(getText().substring(0, getCaretPosition()) + AttributedString.cancel +
            getText().substring(getCaretPosition(), getText().length()));
          setCaretPosition(caretpos);
          e.consume();
          return;
        case 82: // control-r reverse
          setText(getText().substring(0, getCaretPosition()) + AttributedString.reverse +
            getText().substring(getCaretPosition(), getText().length()));
          setCaretPosition(caretpos);
          e.consume();
          return;
        default:
      }
    }

    if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() != 0) {
      this.resetIterator();
      event.text = getText();
      fireInputEvent();
      e.consume();
      return;
    }

    //
    // deal with arrow up
    //
    if (e.getKeyCode() == KeyEvent.VK_UP) {

      // See if there is an available command in list
      if (commandIterator.hasPrevious()) {

        // Store it
        String previous = String.valueOf(commandIterator.previous());

        // See if we should skip one forward
        if (!previousKeyUpArrow && commandIterator.hasPrevious()) {
          setText(String.valueOf(commandIterator.previous()));
        }

        // Set text from history
        else {
          setText(previous);
        }

      } else {
        e.consume();
      }

      // Set flag
      this.previousKeyUpArrow = true;
    }

    // deal with arrow down
    if (e.getKeyCode() == KeyEvent.VK_DOWN) {

      // This will hold the next item in the list
      String next;

      // Check
      if (commandIterator.hasNext()) {

        // Special case check, user pressed up and down from empty
        // command line
        if (commandIterator.hasNext() &&
          commandIterator.nextIndex() + 1 == commandHistory.size() &&
          this.previousKeyUpArrow) {
          // Clear the command textfield and reset iterator
          resetIterator(false);
          this.previousKeyUpArrow = true;
          setText("");
        }

        // Not a special case
        else {
          // Fetch next item in command history (downwards)
          next = String.valueOf(commandIterator.next());

          // Semi-special case, user pressed up then down so
          // we need to skip an item
          if (previousKeyUpArrow && commandIterator.hasNext()) {
            setText(String.valueOf(commandIterator.next()));
          }

          // Just set the text
          else {
            setText(next);
          }

          // Set flag (user pressed arrow down)
          this.previousKeyUpArrow = false;
        }
      }

      // No more items in history, clear the textfield and set flag
      else {
        setText("");
        next = null;
        this.previousKeyUpArrow = true;

      }

      // I'm not really sure what this is
      e.consume();
    }

    // deal with ^K and other built in shortcuts
  }

  // Resets to the beginning of the command iterator
  private void resetIterator() {
    this.resetIterator(true);
  }

  // Reset the iterator to beginning (true) or end (false)
  private void resetIterator(boolean resetToBeginning) {
    if (resetToBeginning) {

      // Reset to beginning
      while (this.commandIterator.hasPrevious()) {
        this.commandIterator.previous();
      }

    } else {

      // Reset to end
      while (this.commandIterator.hasNext()) {
        this.commandIterator.next();
      }

    }
  }


  private void addToHistory(String text) {

    // Add the text to the command history and remove redundant items
    this.commandExistsInHistory(text, true); // Remove if it already exists
    this.resetIterator(false);   // Reset to end
    this.commandIterator.add(text); // Append to end of list

    // Check if the max size of the history has been reached
    if (this.commandHistory.size() == (maxCommands + 1)
      && (maxCommands > 0)) {

      this.resetIterator(); // Reset to beginning
      this.commandIterator.remove(); // Remove first item in list
      this.resetIterator(false);   // Reset to end
    }
  }


  // Returns true if the command is found in the history. If the
  // parameter <i>delete</i> is true, the command is deleted if found.
  // This method offers O(n) performance.
  private boolean commandExistsInHistory(String cmd, boolean delete) {

    // Reset iterator to beginning
    this.resetIterator();

    // Iterate through the history from beginning to end
    while (this.commandIterator.hasNext()) {
      if (this.commandIterator.next().equals(cmd)) {

        // Command was found
        if (delete) {

          // Remove it
          this.commandIterator.remove();
        }

        // Return
        return true;
      }
    }

    // Not found
    return false;
  }

  public void keyReleased(KeyEvent e) {
  }

  public void paint(Graphics g) {
    TextSource.initGraphics(g);
    super.paint(g);
  }

  protected Document createDefaultModel() {
    return new InputDocument();
  }

  class InputDocument extends PlainDocument {
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str.indexOf('\n') == -1) {
//              super.insertString(offs, str, a);
        super.insertString(offs, str, a);
        return;
      }

      while (str.indexOf('\n') > -1) {
        event.text = str.substring(0, str.indexOf('\n'));
        // @Serge: multiple lines may appear in the input field due to paste.
        // First line from the pasted text should be inserted into the
        // current position and then the whole line should be processed,
        // otherwise current field text will be lost!
        if (getCurrentText().length() > 0) {
          super.insertString(offs, event.text, a);
          event.text = getCurrentText();
        }

        fireInputEvent();

        if (str.indexOf('\n') == str.length()) {
          return;
        } else {
          str = str.substring(str.indexOf('\n') + 1, str.length());
        }
      }

      if (str.length() > 0) {
        event.text = str;
        fireInputEvent();
      }
    }
  }
}
