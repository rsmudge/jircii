package text;

import rero.util.*;

public class AttributedString
{
    public static final char bold      = (char)2; /** constant for defacto irc standard bold character */
    public static final char underline = (char)31; /** constant for defacto irc standard underline character */
    public static final char color     = (char)3; /** constant for defacto irc standard color character (thanks mIRC) */
    public static final char cancel    = (char)15; /** constant for defacto irc standard cancel character */
    public static final char reverse   = (char)22; /** constant for defacto irc standard reverse character */
    public static final char tab       = '\t'; /** constant for beloved tab character */

    protected AttributedText  attrs;   // linked list of string and its attributes
    protected TokenizedString tokens;  // cached tokenized version of the string (possibly to not be cached)
    protected String          text;    // stripped version of the string

//    public static int total_instances = 0;

    /** constructor called for an already parsed attributed string, if you want to create a new object use CreateAttributedString */
    public AttributedString(String _text, AttributedText _attrs)
    {
        this(_text, _attrs, " ");
    }

    /** constructor called for an already parsed attributed string, if you want to create a new object use CreateAttributedString */
    public AttributedString(String _text, AttributedText _attrs, String delimeter)
    {
        text   = _text;
        attrs  = _attrs;
        tokens = new TokenizedString(text, delimeter);
//        total_instances++;
    }

/*    protected void finalize()
    {
        total_instances--;
    } */

    public String getText()
    {
        return text;
    }

    public TokenizedString getTokens()
    {
        return tokens;
    }

    public AttributedText getAttributedText()
    {
        return attrs;
    }

    public AttributedText substring(int start, int end)
    {
        return substring(attrs, start, end);
    }

    public void assignWidths()
    {
        AttributedText head = attrs;
        while (head != null)
        {
           head.width = TextSource.fontMetrics.stringWidth(head.text);
           head = head.next;
        }

    }

    // adjust start and end as we go...

    public AttributedText substring(AttributedText original, int start, int end)
    {
        AttributedText head;
        AttributedText temp = original;

        while (temp != null)
        {
           if (start > temp.start && end <= temp.end)
           {
              // special case, string range is just one type of attribute
              head = temp.copyAttributes();
              head.text  = text.substring(start, end);
              head.width = TextSource.fontMetrics.stringWidth(head.text);
              return head;
           }
           else if (start <= temp.start && end >= temp.end)
           {
              head       = temp.copyAttributes();
              head.text  = temp.text;
              head.width = TextSource.fontMetrics.stringWidth(head.text);
              head.next  = substring(temp.next, start, end);
              return head;
           }
           else if (start <= temp.start && (end <= temp.end && end > temp.start))
           {
              head       = temp.copyAttributes();
              head.text  = text.substring(temp.start, end);
              head.width = TextSource.fontMetrics.stringWidth(head.text);
              return head;
           }
           else if (start > temp.start && start <= temp.end && end >= temp.end)
           {
              head       = temp.copyAttributes();
              head.text  = text.substring(start, temp.end);
              head.width = TextSource.fontMetrics.stringWidth(head.text);
              head.next  = substring(temp.next, start, end);
              return head;
           }

           temp = temp.next;
        }
       
        return null;
    }

   /** attributed string parser, it just doesn't get to be more fun than this */
   public static AttributedString CreateAttributedString(String text)
   {
      return CreateAttributedString(text, " ");
   }

   public static AttributedString CreateAttributedString(String text, String delim)
   {
      AttributedText current  = new AttributedText();
      StringBuffer   result   = new StringBuffer();
      StringBuffer   stripped = new StringBuffer();

      AttributedText head    = current;

      char[] data = text.toCharArray();

      int fore, back; // foreground and background color indices (temporary variables)

      for (int x = 0; x < data.length; x++)
      {
         switch (data[x])
         {
            case bold:
              if (result.length() > 0)
              {
                 current.text = result.toString();
                 current.end  = current.start + current.text.length();
                 result = new StringBuffer();
          
                 current.next = current.copyAttributes();
                 current.next.start = current.end;
                 current = current.next;
              }
              current.isBold = !current.isBold;
              break;
            case color:
              back  = current.backIndex; // take a cue from the current background.
              fore  = current.foreIndex; // take a cue from the current foreground.

              if ((x + 1) < data.length && Character.isDigit(data[x+1]))
              {
                 if ((x + 2) < data.length && Character.isDigit(data[x+2]))
                 {
                    fore = (Character.digit(data[x+1], 10) * 10) + Character.digit(data[x+2], 10);
                    x+=2;
                 }
                 else
                 {
                    fore = Character.digit(data[x+1], 10);
                    x++;
                 }

                 if ((x + 2) < data.length && data[x+1] == ',' && Character.isDigit(data[x+2]))
                 {
                    x++;

                    if ((x + 2) < data.length && Character.isDigit(data[x+2]))
                    {
                       back = (Character.digit(data[x+1], 10) * 10) + Character.digit(data[x+2], 10);
                       x+=2;
                    }
                    else
                    {
                       back = Character.digit(data[x+1], 10);
                       x++;
                    }
                 }
              }

              if (fore != current.foreIndex || back != current.backIndex)
              {
                 if (result.length() > 0)
                 {
                    current.text = result.toString();
                    current.end  = current.start + current.text.length();
                    result = new StringBuffer();

                    current.next = current.copyAttributes();
                    current.next.start = current.end;
                    current = current.next;
                 }

                 current.foreIndex = fore;
                 current.backIndex = back;
              }
              break;
            case cancel:
              if (result.length() > 0)
              {
                 current.text = result.toString();
                 current.end  = current.start + current.text.length();
                 result = new StringBuffer();

                 current.next = (new AttributedText()).copyAttributes();
                 current.next.foreIndex = current.foreIndex;
                 current.next.start = current.end;
                 current = current.next;
              }
              else
              {
//                 current = (new AttributedText()).copyAttributes();
                 current.isBold = false;
                 current.isUnderline = false;
                 current.isReverse = false;
                 current.backIndex = -1;
                 current.foreIndex = 0;
              }
              break;
            case reverse:
              if (result.length() > 0)
              {
                 current.text = result.toString();
                 current.end  = current.start + current.text.length();
                 result = new StringBuffer();

                 current.next = current.copyAttributes();
                 current.next.start = current.end;
                 current = current.next;
              }
              current.isReverse = !current.isReverse;
              break;
            case underline:
              if (result.length() > 0)
              {
                 current.text = result.toString();
                 current.end  = current.start + current.text.length();
                 result = new StringBuffer();

                 current.next = current.copyAttributes();
                 current.next.start = current.end;
                 current = current.next;
              }
              current.isUnderline = !current.isUnderline;
              break;
            case tab:
              result.append("   ");
              stripped.append("   ");
              break;
            default:
              result.append(data[x]);
              stripped.append(data[x]);
         } 
      }
      current.text = result.toString();
      current.end  = current.start + current.text.length();
 
      return new AttributedString(stripped.toString(), head, delim);
   }
}
