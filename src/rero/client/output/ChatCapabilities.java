package rero.client.output;

import rero.client.Feature;
import rero.dcc.Chat;
import rero.dcc.DataDCC;
import rero.dcc.ProtocolDCC;
import rero.ircfw.InternalDataList;
import rero.net.SocketConnection;
import rero.util.ClientUtils;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class ChatCapabilities extends Feature {
  protected SocketConnection sock;
  protected OutputCapabilities output;
  protected DataDCC dccData;

  public void init() {
    sock = getCapabilities().getSocketConnection();
    output = getCapabilities().getOutputCapabilities();
    dccData = (DataDCC) getCapabilities().getDataStructure("dcc");
  }

  public void sendMessage(String nickname, String message) {
    HashMap eventData = new HashMap();

    if (nickname.charAt(0) == '=') {
      String dnickname = nickname.substring(1, nickname.length());
      Chat connection = (Chat) dccData.getSpecificConnection(dnickname, ProtocolDCC.DCC_CHAT);

      if (connection == null) {
        eventData.put("$target", dnickname);
        eventData.put("$parms", message);
        eventData.put("$data", dnickname + " " + message);
        output.fireSetTarget(eventData, nickname, "SEND_CHAT_ERROR");
        return;
      }

      connection.sendln(message);
    } else {
      int max = computeMaxMessageLength(nickname);
      if (message.length() > max) {
        // split and send as multiple messages
        String[] messages = wrapMessageToStringsArray(message, max);
        for (int i = 0; i < messages.length; i++) {
          String msg = messages[i];
          sock.println("PRIVMSG " + nickname + " :" + msg);
        }
      } else {
        sock.println("PRIVMSG " + nickname + " :" + message);
      }
    }

    if (nickname.charAt(0) == '=') {
      eventData.put("$target", nickname.substring(1, nickname.length()));
      eventData.put("$parms", message);
      eventData.put("$data", nickname.substring(1, nickname.length()) + " " + message);

      output.fireSetTarget(eventData, nickname, "SEND_CHAT");
    } else if (ClientUtils.isChannel(nickname)) {
      eventData.put("$target", nickname);
      eventData.put("$channel", nickname);
      eventData.put("$parms", message);
      eventData.put("$data", nickname + " " + message);

      output.fireSetTarget(eventData, nickname, output.chooseSet(nickname, "SEND_TEXT", "SEND_TEXT_INACTIVE"));
    } else {
      eventData.put("$target", nickname);
      eventData.put("$parms", message);
      eventData.put("$data", nickname + " " + message);

      output.fireSetQuery(eventData, nickname, nickname, "SEND_MSG");
    }
  }

  private int computeMaxMessageLength(String nickname) {
    InternalDataList ci = ((InternalDataList) output.getCapabilities().getDataStructure("clientInformation"));
    String fullAddress = ci.getMyUser().getFullAddress();
    /* maximum allowed message text */
    /* :nickname!username@host.com PRIVMSG #channel :text\r\n */
    int max = 512;
    max -= 14;  /* :, " PRIVMSG ", " ", :, \r, \n */
    max -= fullAddress.length();
    max -= nickname.length();
    return max;
  }

  public void sendNotice(String target, String message) {
    sock.println("NOTICE " + target + " :" + message);

    HashMap eventData = new HashMap();

    eventData.put("$target", target);
    eventData.put("$parms", message);
    eventData.put("$data", target + " " + message);

    output.fireSetConfused(eventData, target, "notice", "SEND_NOTICE");
  }

  public void sendAction(String target, String message) {
    HashMap eventData = new HashMap();

    if (target.charAt(0) == '=') {
      eventData.put("$target", target.substring(1));
      eventData.put("$parms", message);
      eventData.put("$data", target.substring(1) + " " + message);

      output.fireSetTarget(eventData, target, output.chooseSet(target, "SEND_ACTION", "SEND_ACTION_INACTIVE"));
    } else if (ClientUtils.isChannel(target)) {
      eventData.put("$target", target);
      eventData.put("$channel", target);
      eventData.put("$parms", message);
      eventData.put("$data", target + " " + message);

      output.fireSetTarget(eventData, target, output.chooseSet(target, "SEND_ACTION", "SEND_ACTION_INACTIVE"));
    } else {
      eventData.put("$target", target);
      eventData.put("$parms", message);
      eventData.put("$data", target + " " + message);

      output.fireSetTarget(eventData, target, "SEND_ACTION");
    }

    if (target.charAt(0) == '=') {
      Chat connection = (Chat) dccData.getSpecificConnection(target.substring(1), ProtocolDCC.DCC_CHAT);

      if (connection == null) {
        output.fireSetTarget(eventData, target, "SEND_CHAT_ERROR");
        return;
      }

      connection.sendln((char) 1 + "ACTION " + message + (char) 1);
    } else {
      sock.println("PRIVMSG " + target + " :" + (char) 1 + "ACTION " + message + (char) 1);
    }
  }

  public void sendRequest(String target, String type, String parms) {
    HashMap eventData = new HashMap();

    eventData.put("$target", target);
    eventData.put("$parms", parms);
    eventData.put("$type", type);
    eventData.put("$data", target + " " + type + " " + parms);

    if (type.equals("PING") && parms.equals("")) {
      parms = System.currentTimeMillis() + "";
    }

    output.fireSetConfused(eventData, target, "reply", "SEND_CTCP");

    if (parms.length() > 0) {
      sock.println("PRIVMSG " + target + " :" + (char) 1 + type.toUpperCase() + " " + parms + (char) 1);
    } else {
      sock.println("PRIVMSG " + target + " :" + (char) 1 + type.toUpperCase() + (char) 1);
    }
  }

  public void sendReply(String target, String type, String parms) {
    sock.println("NOTICE " + target + " :" + (char) 1 + type.toUpperCase() + " " + parms + (char) 1);
  }


  public static String[] wrapMessageToStringsArray(String message, int length) {
    String s = wrapText(message, "\n", length);
    return s.split("\n");
  }

  /**
   * Takes a block of text which might have long lines in it and wraps the long lines based on the supplied wrapColumn
   * parameter. It was initially implemented for use by VelocityEmail. If there are tabs in inString, you are going to
   * get results that are a bit strange, since tabs are a single character but are displayed as 4 or 8 spaces. Remove
   * the tabs.
   *
   * @param inString   Text which is in need of word-wrapping.
   * @param newline    The characters that define a newline.
   * @param wrapColumn The column to wrap the words at.
   * @return The text with all the long lines word-wrapped.
   */
  public static String wrapText(String inString, String newline,
                                int wrapColumn) {
    StringTokenizer lineTokenizer = new StringTokenizer(
      inString, newline, true);
    StringBuffer stringBuffer = new StringBuffer();

    while (lineTokenizer.hasMoreTokens()) {
      try {
        String nextLine = lineTokenizer.nextToken();

        if (nextLine.length() > wrapColumn) {
          // This line is long enough to be wrapped.
          nextLine = wrapLine(nextLine, newline, wrapColumn);
        }

        stringBuffer.append(nextLine);
      }
      catch (NoSuchElementException nsee) {
        // thrown by nextToken(), but I don't know why it would
        break;
      }
    }

    return (stringBuffer.toString());
  }

  /**
   * Wraps a single line of text. Called by wrapText(). I can't think of any good reason for exposing this to the
   * public, since wrapText should always be used AFAIK.
   *
   * @param line       A line which is in need of word-wrapping.
   * @param newline    The characters that define a newline.
   * @param wrapColumn The column to wrap the words at.
   * @return A line with newlines inserted.
   */
  protected static String wrapLine(String line, String newline,
                                   int wrapColumn) {
    StringBuffer wrappedLine = new StringBuffer();

    while (line.length() > wrapColumn) {
      int spaceToWrapAt = line.lastIndexOf(' ', wrapColumn);

      if (spaceToWrapAt >= 0) {
        wrappedLine.append(line.substring(0, spaceToWrapAt));
        wrappedLine.append(newline);
        line = line.substring(spaceToWrapAt + 1);
      } else {
        // if no space found, force wrap at wrapColumn
        spaceToWrapAt = wrapColumn;
        wrappedLine.append(line.substring(0, spaceToWrapAt));
        wrappedLine.append(newline);
        line = line.substring(spaceToWrapAt);
      }
    }
    // Whatever is left in line is short enough to just pass through,
    // just like a small small kidney stone
    wrappedLine.append(line);

    return wrappedLine.toString();
  }

  /*public static void main(String[] args) {
    String[] strings = wrapMessageToStringsArray("alpha beta gamma someshit", 6);
    for (int i = 0; i < strings.length; i++) {
      String string = strings[i];
      System.out.println("string = " + string);
    }
  }*/
}


