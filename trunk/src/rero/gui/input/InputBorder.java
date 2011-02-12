package rero.gui.input;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import text.*;

public class InputBorder implements Border
{
    protected AttributedString indent;
    protected String           original;

    public InputBorder(String text)
    {
       indent = AttributedString.CreateAttributedString(text);
       indent.assignWidths();

       original = text;
    }

    public String getText()
    {
       return original;
    } 

    public AttributedText getAttributes()
    {
       return indent.getAttributedText();
    }

    public boolean isBorderOpaque()
    {
       return true;
    }

    public Insets getBorderInsets(Component c)
    {
       return new Insets(0, indent.getAttributedText().getWidth(), 0, 0); 
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
       TextSource.initGraphics(g);
       TextSource.drawText(g, indent.getAttributedText(), x, height + y - 4); // was -4 
    }
}
