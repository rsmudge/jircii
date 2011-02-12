package rero.ircfw.data;

/* keeps track of parts, joins, quits, kicks, and nick changes for everyone but me...

   the IDL API makes dealing with this aspect of things pretty easy.
   I may consider refactoring the data list API's for this stuff into this object,
   we'll see...   */

import rero.ircfw.*;
import rero.ircfw.interfaces.*;

import java.util.*;

public class ChannelUserWatch extends DataEventAction implements FrameworkConstants
{
    public boolean isEvent(HashMap data)
    {
        String event = (String)data.get($EVENT$);
         
        return (event.equals("QUIT") || event.equals("JOIN") || event.equals("PART") || 
                event.equals("NICK") || event.equals("KICK") || event.equals("PRIVMSG")) ;
    }

    public void process(HashMap data)
    {
        String event   = (String)data.get($EVENT$);
        String nick    = (String)data.get($NICK$);
        String channel = (String)data.get($TARGET$);

        if (event.equals("JOIN"))
        {
            dataList.JoinNick(nick, channel);
        }
        else if (event.equals("PART"))
        {
            dataList.PartNick(nick, dataList.getChannel(channel));
        }
        else if (event.equals("QUIT"))
        {
            StringBuffer blah = new StringBuffer();

            Iterator iter = dataList.getUser(nick).getChannels().iterator();
            while (iter.hasNext())
            {
               blah.append(((Channel)iter.next()).getName());

               if (iter.hasNext())
                  blah.append(",");
            }

            data.put("$channels", blah.toString());

            dataList.QuitNick(nick);
        }
        else if (event.equals("NICK") && data.containsKey($PARMS$))
        {
            dataList.ChangeNick(nick, (String)data.get($PARMS$));
        }
        else if (event.equals("KICK"))
        { 
            String d = (String)data.get($PARMS$);
            d = d.substring(0, d.indexOf(' '));
            dataList.PartNick(d, dataList.getChannel(channel));
        }     
        else if (event.equals("PRIVMSG"))
        {
            // @Serge: code moved to ProcessEvents.java, where we can touch user not only on
            // PRIVMSG, but also on ACTION and other events.
            // Fix for: http://jirc.hick.org/cgi-bin/bitch.cgi/view.html?1912086
        }
    }
}

/*
Enter String> :theUncle!~a@pcp.royalok.mi.comcast.net NICK :^butane
  *** looking at: :^butane
Parsing message took:  8
  user = ~a
  data = <null> ^butane
  host = pcp.royalok.mi.comcast.net
  address = ~a@pcp.royalok.mi.comcast.net
  event = NICK
  source = theUncle
  nick = theUncle
  raw = :theUncle!~a@pcp.royalok.mi.comcast.net NICK :^butane
  parms = ^butane

Enter String> :cheezi!chris@likes.dick.com KICK #pollution ^butane :I am the uncle
  *** looking at: ^butane :I am the uncle
  *** looking at: :I am the uncle
Parsing message took:  2
  user = chris
  data = #pollution ^butane I am the uncle
  target = #pollution
  host = likes.dick.com
  address = chris@likes.dick.com
  event = KICK
  source = cheezi
  nick = cheezi
  raw = :cheezi!chris@likes.dick.com KICK #pollution ^butane :I am the uncle
  parms = ^butane I am the uncle

*/
