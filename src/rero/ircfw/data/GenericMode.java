package rero.ircfw.data;

import java.util.*;

public class GenericMode
{
    String modes = "+";

    public void SetMode(char m)
    {
        if (modes.indexOf(m) == -1) 
        {
            modes = modes + m;
        }
    }

    public boolean isSet(char m)
    {
        return (modes.indexOf(m) > -1);
    }

    public GenericMode(String m)
    {
        modes = m;
    }

    public GenericMode()
    {

    }

    public String toString()
    {
        return modes;
    }

    public void UnsetMode(char m)
    {
        if (modes.indexOf(m) > -1)
        {
            StringBuffer temp = new StringBuffer();
            for (int x = 0; x < modes.length(); x++)
            {
                if (modes.charAt(x) != m)
                {
                   temp.append(modes.charAt(x));
                }
            }

            modes = temp.toString();
        }
    }
}
