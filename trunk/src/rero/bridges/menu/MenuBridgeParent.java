package rero.bridges.menu;

import javax.swing.*;

public interface MenuBridgeParent
{
   public void addSeparator();
   public JMenuItem add(JMenuItem menuItem);
}
