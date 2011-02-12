package rero.util;

import java.util.regex.*;

public class StringParser
{
    Pattern myPattern;
    Matcher myMatcher;

    public StringParser( String matchMe, Pattern aPattern )
    {
        myPattern = aPattern;
        myMatcher = aPattern.matcher(matchMe);
    }

    public String getParsedString(int index)
    {
        return myMatcher.group(index + 1);
    } 

    public String[] getParsedStrings()
    {
        String[] returnValue = new String[myMatcher.groupCount()];
        for (int x = 0; x < returnValue.length; x++)
        {
            returnValue[x] = myMatcher.group(x + 1);
        }
        return returnValue;
    }

    public boolean matches()
    {
        return myMatcher.matches();
    }
}
