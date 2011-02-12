package rero.gui.script;

import rero.client.output.*;
import java.util.*;

import rero.ircfw.interfaces.ChatListener;

import rero.gui.*;

import rero.util.ClientUtils;

import rero.bridges.event.*;

import rero.gui.input.*;

public class WindowInputListener extends ScriptedEventListener implements InputListener
{
   public void onInput(UserInputEvent ev)
   {
      HashMap eventData = ClientUtils.getEventHashMap("-", ev.text);

      if (dispatchEvent(eventData) == rero.ircfw.interfaces.ChatListener.EVENT_HALT)
      {
         ev.consume();
      }
   }

   public void setupListener()
   {
      // already setup by default *shrug*
   }
}
