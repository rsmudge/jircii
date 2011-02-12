package rero.ircfw.data;

import rero.ircfw.*;
import java.util.HashMap;

public abstract class DataEventAction
{
    InternalDataList dataList;

    public void passStructures(InternalDataList d)
    {
       dataList = d;
    }

    public abstract boolean isEvent(HashMap data);
    public abstract void process(HashMap data);
}
