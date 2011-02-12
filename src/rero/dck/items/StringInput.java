package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.config.*;
import rero.dck.*;

public class StringInput extends SuperInput
{
   protected JLabel label;
   protected String value;
   protected JTextField text;

   public StringInput(String var, String _value, String _label, int rightGap, char mnemonic)
   {
      label = new JLabel(_label);

      setLayout(new BorderLayout());
     
      text = new JTextField();
 
      add(label, BorderLayout.WEST);
      add(text, BorderLayout.CENTER);
      
      if (rightGap > 0)
      {
         JPanel temp = new JPanel();
         temp.setPreferredSize(new Dimension(rightGap, 0));

         add(temp, BorderLayout.EAST);
      }

      label.setDisplayedMnemonic(mnemonic);

      variable = var;
      value = _value;
   }

   public void save()
   {
      ClientState.getClientState().setString(getVariable(), text.getText());
   }

   public int getEstimatedWidth()
   {
      return (int)label.getPreferredSize().getWidth();
   }

   public void setAlignWidth(int width)
   {
      label.setPreferredSize(new Dimension(width, 0));
      revalidate();
   }

   public JComponent getComponent()
   {
      return this;
   }

   public void refresh()
   {
      text.setText(ClientState.getClientState().getString(getVariable(), value));
   }
}


