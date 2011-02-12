package rero.ircfw.data;

/* the mode parser... code like this always makes my day (not!) */

import rero.ircfw.Channel;
import rero.ircfw.interfaces.FrameworkConstants;

import java.util.HashMap;

public class ModeTracker extends DataEventAction implements FrameworkConstants {
  public boolean isEvent(HashMap data) {
    return ("MODE".equals(data.get($EVENT$)) || "324".equals(data.get($EVENT$)) || "221".equals(data.get($EVENT$)));
  }

  public void process(HashMap data) {
    String event = (String) data.get($EVENT$);
    String target = "", modes = "";

    if (event.equals("MODE") || event.equals("221")) {
      target = (String) data.get($TARGET$);
      modes = (String) data.get($PARMS$);
      data.put($PARMS$, modes.trim()); // should fix extra characters being appended to the modes :)
    } else if (event.equals("324")) {
      String[] blah = data.get($PARMS$).toString().split("\\s", 2);

      target = blah[0];
      modes = blah[1];
    }

    boolean isChannel = (target.charAt(0) == '#' || target.charAt(0) == '&');
    boolean doSet = false;

    String parse[] = modes.split("\\s", 0);

    modes = parse[0];

    int target_index = 1; /* index of current target in parse[] array */

    for (int x = 0; x < modes.length(); x++) {
      if (modes.charAt(x) == '+') {
        doSet = true;
      } else if (modes.charAt(x) == '-') {
        doSet = false;
      } else if (isChannel) {
        Channel ch = dataList.getChannel(target);

        if (dataList.getPrefixInfo().isPrefixMode(modes.charAt(x))) {
          dataList.synchronizeUserPreChange(dataList.getUser(parse[target_index]), ch);

          int temp = dataList.getUser(parse[target_index]).getModeFor(ch);

          if (doSet) {
            dataList.getUser(parse[target_index])
              .setModeFor(ch, dataList.getPrefixInfo().setMode(temp, modes.charAt(x)));
          } else {
            dataList.getUser(parse[target_index])
              .setModeFor(ch, dataList.getPrefixInfo().unsetMode(temp, modes.charAt(x)));
          }

          dataList.synchronizeUserPostChange(dataList.getUser(parse[target_index]), ch);
          target_index++;
        } else {
          switch (modes.charAt(x)) {
            case 'l':
              if (doSet) {
                ch.getMode().SetMode('l');
                ch.setLimit(Integer.parseInt(parse[target_index]));
                target_index++;
              } else {
                ch.getMode().UnsetMode('l');
                ch.setLimit(-1);
              }
              break;

            case 'k':
              if (doSet) {
                ch.getMode().SetMode('k');
                ch.setKey(parse[target_index]);
                target_index++;
              } else {
                ch.getMode().UnsetMode('k');
                target_index++;
              }
              break;

            default:
              // modes from group A (such as b) operate user lists, always have
              // params and are not handled here
              if (dataList.isChanGroupMode("A", modes.charAt(x))) {
                target_index++;
                break;
              }

              // modes from group B (such as k) always have a param,
              // only k is handled above at the moment, for all the rest just
              // skip the target and set mode for channel
              if (dataList.isChanGroupMode("B", modes.charAt(x))) {
                target_index++;
              }

              // modes from group C (such as l) only have params when set,
              // so we need to adjust the index when mode is set
              if (dataList.isChanGroupMode("C", modes.charAt(x)) && doSet) {
                target_index++;
              }

              // modes from group D have no params and we don't need to adjust
              // the target index

              if (doSet) {
                ch.getMode().SetMode(modes.charAt(x));
              } else {
                ch.getMode().UnsetMode(modes.charAt(x));
              }
          }
        }
      } else {
        if (!target.equals(dataList.getMyNick())) {
          return;
        }

        if (doSet) {
          dataList.getMyUserInformation().getMode().SetMode(modes.charAt(x));
        } else {
          dataList.getMyUserInformation().getMode().UnsetMode(modes.charAt(x));
        }
      }
    }
  }
}
