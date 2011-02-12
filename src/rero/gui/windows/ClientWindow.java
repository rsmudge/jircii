package rero.gui.windows;

import java.awt.*;
import javax.swing.*;

public interface ClientWindow
{
    public void setTitle(String title);
    public void setContentPane(Container c);

    public void setIcon(ImageIcon i);

    public void show();

    public void addWindowListener(ClientWindowListener l);
    public String getTitle();

    public boolean isSelected();

    public void closeWindow();

    public void setMaximum(boolean b);
    public void setIcon(boolean b);

    public boolean isMaximum();
    public boolean isIcon();

    public void activate();
}
