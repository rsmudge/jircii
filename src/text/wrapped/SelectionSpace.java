package text.wrapped;

import java.awt.*;
import text.*;

public class SelectionSpace
{
    protected boolean ready = false;
    protected int origin_x;
    protected int origin_y;

    protected int start_sel, end_sel;

    protected int x, y;

    boolean flipped;

    protected int lineStart = 0;
    protected int lineEnd = 0;

    protected StringBuffer selection;

    protected Rectangle changed;

    public SelectionSpace(Point p)
    {
       origin_x = (int)p.getX();
       origin_y = (int)p.getY();

       changed = new Rectangle(0, origin_y - ((TextSource.fontMetrics.getHeight() + 2) * 1), 2048, (TextSource.fontMetrics.getHeight() + 2) * 2);

       ready = false;
    }

    public void clear()
    {
       selection = new StringBuffer();
    }

    public void append(String text)
    {
       if (text.length() > 0)
       {
          selection.insert(0, text);
       }
    }

    public void touch()
    {
       if (selection.length() > 0)
       {
           append("\n");
       }
    }

    public String getSelectedText()
    {
       if (selection != null)
       {
          return selection.toString();
       }

       return null;
    }

    protected int translateToLineNumber(int pixely)
    {
       pixely -= 5;
       int height = TextSource.fontMetrics.getHeight() + 2;
       return (pixely - (pixely % height)) / height;
    }

    protected int translateToPixel(int lineno)
    {
       return (lineno * (TextSource.fontMetrics.getHeight() + 2)) + 5;
    }

    public boolean isSelectedLine(int y)
    {
       int temp = translateToLineNumber(y);
       return (ready && temp  >= lineStart && temp <= lineEnd);
    }

    public boolean isOnlyLine(int y)
    { 
       return isStartLine(y) && isEndLine(y);
    }
    
    public boolean isEndLine(int y)
    {
       int temp = translateToLineNumber(y);
       return (ready && temp == lineEnd);
    }

    public boolean isStartLine(int y)
    {
       int temp = translateToLineNumber(y);
       return (ready && temp == lineStart);
    }

    public int getSingleStart()
    {
       if (start_sel < end_sel)
       {
           return start_sel;
       }
       return end_sel;
    }

    public int getSingleEnd()
    {
       if (start_sel < end_sel)
       {
           return end_sel;
       }
       return start_sel;
    }

    public int getSelectionStart()
    {
       return start_sel;
    }
 
    public int getSelectionEnd()
    {
       return end_sel;
    }

    public Rectangle getChangedArea()
    {
       return changed;
    }

    public void growSelection(Point p)
    {
       ready = true;

       x = (int)p.getX();
       y = (int)p.getY();

       // handle normal selection calculations
       if (y < origin_y)
       {
          flipped = true;
       }
       else
       {
          flipped = false;
       }
 
       if (flipped)
       {
          lineStart = translateToLineNumber(y);
          lineEnd   = translateToLineNumber(origin_y);
       }
       else
       {
          lineStart = translateToLineNumber(origin_y);
          lineEnd   = translateToLineNumber(y);
       }

       if (flipped)
       {
          start_sel = x;
          end_sel   = origin_x;
       }
       else
       {
          start_sel = origin_x;
          end_sel   = x;
       } 


       // calculate area that we are going to repaint in (kind of an optimization for selection *uNF* 
//       if (!changed.contains(1024, y))
  //     {
          int start = translateToPixel(lineStart - 1);

          if (start > changed.getY())
          {
             start = (int)changed.getY();
          }

          int end   = translateToPixel(lineEnd + 2) - start;   

          if (end < changed.getHeight())
          {
             end = (int)changed.getHeight();
          }

          changed.setBounds(0, start, 2048, end);
    //   }
    }
}
