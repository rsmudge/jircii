package rero.ircfw;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class is a mode interpreter for each server.  Basically each server can have channel user prefix modes. The
 * state of these modes is stored as an int within each User object (associated in a hashmap with the Channel object as
 * the key).  This class is used to make sense of the integer mode value for a given servers configuration
 */
public class UserMode {
  private String modes; // the actual mode characters i.e. ov
  private String chars; // the actual display characters i.e. @+

  public UserMode(String m, String c) {
    modes = m;
    chars = c;
  }

  public Set getUsersWithMode(Channel channel, char mode) {
    Set copy = new LinkedHashSet();
    Iterator i = channel.getAllUsers().iterator();
    while (i.hasNext()) {
      User temp = (User) i.next();
      int m = temp.getModeFor(channel);

      if (isMode(m, mode)) {
        copy.add(temp);
      }
    }

    return copy;
  }

  public boolean isPrefixMode(char m) {
    return modes.indexOf(m) > -1;
  }

  public boolean isPrefixChar(char m) {
    return chars.indexOf(m) > -1;
  }

  public char getDisplayForMode(char mode) {
    return chars.charAt(modes.indexOf(mode));
  }

  public char getModeForDisplay(char display) {
    return modes.charAt(chars.indexOf(display));
  }

  public int getValueFor(char mchar) {
    return 1 << (modes.length() - modes.indexOf(mchar));
  }

  public int setMode(int original, char mchar) {
    int value = getValueFor(mchar);
    return original | value;
  }

  public int unsetMode(int original, char mchar) {
    int value = getValueFor(mchar);
    return original & ~(value);
  }

  public boolean isMode(int original, char mchar) {
    if (mchar == ' ' && original == 0)
      return true;

    int value = getValueFor(mchar);
    return ((original & value) == value);
  }


  public String getModes() {
    return modes;
  }

  public String getChars() {
    return chars;
  }

  public String toString(int original) {
    for (int x = 0; x < modes.length(); x++) {
      if (isMode(original, modes.charAt(x))) {
        return String.valueOf(chars.charAt(x));
      }
    }
    return "";
  }

  public String toString() {
    return "[UCM:(" + modes + ")" + chars + "]";
  }

}
