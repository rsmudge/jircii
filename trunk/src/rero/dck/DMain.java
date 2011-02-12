package rero.dck;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

public abstract class DMain extends DContainer
{
   public JComponent setupLayout(JComponent component)
   {
      component.setLayout(new BorderLayout());
      component.add(new JPanel(), BorderLayout.CENTER);
      component.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

      JPanel container = new JPanel();
      container.setLayout(new GridBagLayout());

      component.add(container, BorderLayout.NORTH);

      return container;
   }

   public abstract String getDescription();
}



