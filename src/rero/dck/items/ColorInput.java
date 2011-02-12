package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.dck.*;
import rero.config.*;

public class ColorInput extends SuperInput implements ActionListener
{
   protected SolidIcon colorIcon;
   protected JButton   button;
   protected Color     initial;

   public ColorInput(String _variable, Color _initial, String text, char mnemonic)
   {
      setLayout(new FlowLayout(FlowLayout.CENTER));

      initial  = _initial;
      variable = _variable; 

      colorIcon = new SolidIcon(initial, 18, 18);
      button    = new JButton(text, colorIcon);
      button.setMnemonic(mnemonic);

      button.addActionListener(this);
      add(button);
   }

   public void actionPerformed(ActionEvent ev)
   {
      Color temp = JColorChooser.showDialog(button, "Select color...", colorIcon.getColor());
      if (temp != null)
      {
         colorIcon.setColor(temp);
         button.repaint();
         notifyParent();
      }
   }

   public void save()
   {
      ClientState.getClientState().setColor(getVariable(), colorIcon.getColor());
   }

   public void refresh()
   {
      colorIcon.setColor(ClientState.getClientState().getColor(getVariable(), initial));
      button.repaint();
   }

   public int getEstimatedWidth()
   {
      return 0;
   }

   public void setAlignWidth(int width)
   {
   }


   public JComponent getComponent()
   {
      return this;
   }

   protected static class SolidIcon implements Icon
   {
      private int width, height;
      private Color color;

      public SolidIcon(Color c, int w, int h)
      {
         width = w;
         height = h;
         color = c;
      }

      public Color getColor()
      {
         return color;
      }

      public void setColor(Color c)
      {
         color = c;
      }

      public int getIconWidth() { return width; }
      public int getIconHeight() { return height; }

      public void paintIcon(Component c, Graphics g, int x, int y)
      {
         g.setColor(color);
         g.fillRect(x, y, width-1, height-1);
      }
   }
}

