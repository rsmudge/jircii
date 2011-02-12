package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rero.config.*;
import rero.dck.*;

public class FloatInput extends SuperInput implements ChangeListener
{
   protected JLabel     label;
   protected JTextField text;
   protected JSlider    slider;

   protected float  value;

   public FloatInput(String var, float _value, String _label)
   {
      variable = var;
      value = _value;

      setLayout(new BorderLayout(5, 5));

      label = new JLabel(_label);
       
      add(label, BorderLayout.WEST);

      int defValue = (int)(_value * 100);

      slider = new JSlider(JSlider.HORIZONTAL, 0, 100, defValue);
      slider.setMajorTickSpacing(25);
      slider.setMinorTickSpacing(5);
      slider.setPaintTicks(true);
      slider.setPaintLabels(false);

      slider.addChangeListener(this);

      add(slider, BorderLayout.CENTER);
       
      text = new JTextField();
      text.setEditable(false);
      text.setColumns(4);

      refresh();

      JPanel temp = new JPanel();
      temp.setLayout(new FlowLayout(FlowLayout.CENTER));

      temp.add(text);

      add(temp, BorderLayout.EAST);
   }

   public void stateChanged(ChangeEvent ev)
   {
      if (slider.getValue() == 0)
      {
         text.setText("off");
      }
      else
      {
         text.setText(slider.getValue()+"%");    
      }
      notifyParent();
   }

   public void save()
   {
      ClientState.getClientState().setFloat(getVariable(), (float)slider.getValue() / 100f);
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
      int defValue = (int)(ClientState.getClientState().getFloat(getVariable(), value) * 100);
      slider.setValue(defValue);

      if (defValue == 0)
      {
         text.setText("off");
         return;
      }

      text.setText(defValue + "%");
   }
}


