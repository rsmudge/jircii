package rero.ircfw.data;

/* keep addresses in IDL up to date */

import rero.ircfw.*;
import rero.ircfw.interfaces.FrameworkConstants;

import java.util.*;

public class AddressSucker extends DataEventAction implements FrameworkConstants
{
    public boolean isEvent(HashMap data)
    {
        if ( "352".equals(data.get($EVENT$)) ) { return true; }
        return (data.get($ADDRESS$) != null && data.get($NICK$) != null);
    }

    public void process(HashMap data)
    {
        if ( "352".equals(data.get($EVENT$)) )
        {
            String parms = (String)data.get($PARMS$);
            String tokens[] = parms.split("\\s", 0);
 
            // $PARMS$ = #pollution wtf 12.171.34.184 irc.isprime.com rew H@ 3 wtf
            String nick, address;
            
            nick = tokens[4];
            address = tokens[1] + "@" + tokens[2];

            User temp = dataList.getUser(nick);
            temp.setAddress(address);
        }
        else
        {
            User temp = dataList.getUser((String)data.get($NICK$));
            temp.setAddress((String)data.get($ADDRESS$));
        }
    }
}
