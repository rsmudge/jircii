package rero.ircfw;

import rero.ircfw.data.*;

public class MyUser
{
    public MyUser()
    {

    }

    protected GenericMode myModes = new GenericMode();

    public void setMode(String m) { myModes = new GenericMode(m); }
    public GenericMode getMode() { return myModes; }

    protected long awayTime = 0;

    public void setAway()
    {
        awayTime = System.currentTimeMillis();
    }

    public void setBack()
    {
        awayTime = 0;
    }

    public boolean isAway()
    {
        return (awayTime != 0);
    }

    public long getAwayTime()
    {
        return System.currentTimeMillis() - awayTime;
    }
}
