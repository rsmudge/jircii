package rero.gui.script;

import rero.bridges.event.ScriptedEventListener;
import rero.ircfw.interfaces.ChatListener;
import text.event.ClickEvent;
import text.event.ClickListener;

import java.util.HashMap;
import java.awt.event.MouseEvent;

/**
 * Event listener class that handles double clicks on empty channel area
 */
public class WindowAreaClickListener extends ScriptedEventListener implements ClickListener {
  public void wordClicked(ClickEvent ev) {
    MouseEvent event = ev.getEvent();

    HashMap eventData = new HashMap();
    eventData.put("$item", ev.getContext());
    eventData.put("$mouse", event);
    eventData.put("$clicks", new Integer(event.getClickCount()).toString());

    if (dispatchEvent(eventData) == ChatListener.EVENT_HALT) {
      ev.consume();
    }
  }

  public void setupListener() {
    // already setup by default *shrug*
  }
}
