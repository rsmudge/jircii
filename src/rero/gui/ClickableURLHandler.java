package rero.gui;

import rero.client.Feature;
import rero.config.ClientDefaults;
import rero.config.ClientState;
import rero.util.ClientUtils;
import text.event.ClickEvent;
import text.event.ClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickableURLHandler extends Feature implements ClickListener {
  private static final Pattern CHAN_PATTERN = Pattern.compile("[@+%]*(#.*)");

  public void wordClicked(ClickEvent ev) {
    int clickCount = ev.getEvent().getClickCount();
    boolean dclick = ClientState.getClientState().isOption("dclick.links", ClientDefaults.dclick_links);
    int need_clicks = dclick ? 2 : 1;

    if (clickCount == need_clicks) {
      String item = ev.getClickedText().toLowerCase();

      if (item.matches("^\\(*(http|https|ftp)://.*")) {
        ClientUtils.openURL(extractURL(ev.getClickedText()));
        ev.consume();
        ev.acknowledge();
      } else if (item.matches("^www\\..*")) {
        String location = extractURL(ev.getClickedText());
        ClientUtils.openURL("http://" + location);
        ev.consume();
        ev.acknowledge();
      } else {
        Matcher m = CHAN_PATTERN.matcher(ev.getClickedText());
        if (m.matches()) {
          String chan = m.group(1).trim();
          getCapabilities().sendln("JOIN " + chan);
          ev.consume();
          ev.acknowledge();
        }
      }
    }
  }

  private static String extractURL(String url) {
    if (url.charAt(0) == '(') {
      url = url.substring(1, url.length() - 1);
    }
    return url;
  }

  public void init() {
  }
}
