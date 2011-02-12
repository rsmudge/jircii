package rero.gui.script;

import rero.bridges.event.EventBridge;
import rero.gui.IRCSession;
import rero.gui.windows.ChannelWindow;
import rero.gui.windows.ClientWindowEvent;
import rero.gui.windows.EmptyWindow;
import rero.gui.windows.StatusWindow;

public class UIScriptBridge {
  protected IRCSession session;

  protected WindowStateListener windowState;       // dispatches window state related events (on close,open,minimize etc.)
  protected WindowDataListener windowData;        // dispatches the "on window" event for text being echo'd to a window
  protected WindowClickListener windowClick;       // handles the "on click" event for text being clicked on in a window...
  protected WindowClickListener windowDoubleClick; // handles the "on dclick" event for text being (double) clicked on in a listbox...
  protected WindowClickListener windowSpecialClick; // handles the "on sclick" event for text being (double) clicked on in a special window
  protected WindowInputListener windowInput;       // handles the "on input" event

  protected WindowAreaClickListener areaClickListener;       // handles double clicks for empty channel areas

  protected SessionOperators sessionOps;

  protected WindowOperators windowOps;         // functions related to windows specifically
  protected WindowManagementOperators windowMgmt;        // functions related to windows specifically

  protected UIOperators interfaceOps;

  public UIScriptBridge(IRCSession _session) {
    session = _session;

    EventBridge bridge = (EventBridge) session.getCapabilities().getDataStructure("eventBridge");

    windowState = new WindowStateListener(session);
    windowState.registerListener(bridge);

    windowData = new WindowDataListener(session.getCapabilities().getUserInterface());
    bridge.registerEvent("window", windowData);

    windowClick = new WindowClickListener();
    bridge.registerEvent("click", windowClick);

    windowDoubleClick = new WindowClickListener();
    bridge.registerEvent("dclick", windowDoubleClick);

    areaClickListener = new WindowAreaClickListener();
    bridge.registerEvent("cclick", areaClickListener);

    windowSpecialClick = new WindowClickListener();
    bridge.registerEvent("sclick", windowSpecialClick);

    windowInput = new WindowInputListener();
    bridge.registerEvent("input", windowInput);

    windowOps = new WindowOperators(session);
    session.getCapabilities().getScriptCore().addBridge(windowOps);

    windowMgmt = new WindowManagementOperators(session);
    session.getCapabilities().getScriptCore().addBridge(windowMgmt);

    sessionOps = new SessionOperators(session);
    session.getCapabilities().getScriptCore().addBridge(sessionOps);

    interfaceOps = new UIOperators(session);
    session.getCapabilities().getScriptCore().addBridge(interfaceOps);
  }

  /**
   * called to notify the scripting bridge that a window has been created, this is for listeners and such to get
   * registered
   */
  public void windowCreated(final StatusWindow window) {
    window.getWindow().addWindowListener(windowState);

    if (window.isLegalWindow()) {
      window.getDisplay().addClickListener(windowClick);
      window.getDisplay().addClickListener(areaClickListener);
      window.getInput().addInputListener(windowInput);

      if (window instanceof ChannelWindow) {
        ((ChannelWindow) window).addClickListener(windowDoubleClick);
      }
    } else {
      ((EmptyWindow) window).addClickListener(windowSpecialClick);
    }

    windowState
      .onOpen(new ClientWindowEvent(window.getWindow())); // fake out the scripting saying "the window has opened"
  }
}
