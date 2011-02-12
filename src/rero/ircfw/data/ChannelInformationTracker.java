package rero.ircfw.data;

/*  Keeps Channel Information Up to Date
    (Including: Channel topic, user names (c/o /names reply))
 
 */

import rero.ircfw.*;
import rero.ircfw.interfaces.FrameworkConstants;

import java.util.*;

public class ChannelInformationTracker extends DataEventAction implements FrameworkConstants
{
    public boolean isEvent(HashMap data)
    {
        String event = (String)data.get($EVENT$);
         
        return (event.equals("353") ||    /* numeric giving us a /names reply */
                event.equals("TOPIC") ||  /* TOPIC has been changed... */
                event.equals("332"));     /* numeric giving us the channel topic */
    }

    public void process(HashMap data)
    {
        String event   = (String)data.get($EVENT$);
        String parms   = (String)data.get($PARMS$);
        String channel;

        if (event.equals("332"))
        {
            channel = parms;
            channel = channel.substring(0, channel.indexOf(' '));

            if (dataList.getChannel(channel) != null) 
               dataList.getChannel(channel).setTopic(parms.substring(channel.length() + 1, parms.length()));
        }
        else if (event.equals("TOPIC"))
        {
            channel = (String)data.get($TARGET$);
            dataList.getChannel(channel).setTopic(parms);
        }
        else if (event.equals("353"))
        {
            String[] users = parms.split("\\s", 0);

            Channel ch = dataList.getChannel(users[1]);

            if (!dataList.isOn(dataList.getMyUser(), ch))
            {
                return;
            }

            for (int x = 2; x < users.length; x++)
            {
                dataList.AddUser(users[x], ch);
            }
        }
    }
}
