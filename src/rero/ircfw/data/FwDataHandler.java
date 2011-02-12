package rero.ircfw.data;

import rero.ircfw.*;
import java.util.HashMap;

public class FwDataHandler
{
    protected DataEventAction[]   responders;
    protected InternalDataList    dataList;

    public FwDataHandler()
    {
        /* instantiate lists */
        dataList = new InternalDataList();

        /* instantiate each responder */
        responders = new DataEventAction[6];

        responders[0] = new AddressSucker();
        responders[1] = new ChannelInformationTracker();
        responders[2] = new ChannelUserWatch();
        responders[3] = new MyInformationTracker();        
        responders[4] = new ModeTracker();
        responders[5] = new UserHostParser();

        for (int x = 0; x < responders.length; x++)
        {
            responders[x].passStructures(dataList); 
        }
    }     

    public InternalDataList getDataList()
    {
        return dataList;
    }

    public HashMap parseEvent(HashMap data)
    {
        for (int x = 0; x < responders.length; x++)
        {
            if (responders[x].isEvent(data))
            {
                responders[x].process(data);
            }
        }

        return data;
    }
}
