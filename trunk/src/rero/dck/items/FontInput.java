package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;

import rero.dck.*;
import rero.config.*;

public class FontInput extends SuperInput implements ItemListener
{
   protected JComboBox  name;
   protected JComboBox  style;
   protected JComboBox  size;

   protected JLabel     preview;

   protected Font      value;

   protected boolean   listing = true;

   public static String funny()
   {
      String taglines[] = {
         "The quick brown fox jumped over the lazy coder",
         "Fat butane, grubbin' on French fries",
         "Sun sucks!@",
     };
     int r = ((int)System.currentTimeMillis() / 1000) % taglines.length;
     return taglines[Math.abs(r)];
   }

   public FontInput(String _variable, Font _value)
   {
      variable = _variable;

      value = _value;

      setLayout(new BorderLayout()); 

      JPanel top = new JPanel();
      top.setLayout(new BorderLayout());
      
      JPanel fonts = new JPanel();
      fonts.setLayout(new FlowLayout(FlowLayout.CENTER));
      
      name  = new JComboBox();
      name.setPrototypeDisplayValue("Times New Roman.");
      name.addItem("Loading fonts...");

      style = new JComboBox(new String[] { "Plain", "Italic", "Bold" });      
      size  = new JComboBox(new String[] { "5", "8", "9","10", "11", "12", "13", "14", "15", "16", "17", "18", "20", "22", "26", "32" });

      name.addItemListener(this);
      style.addItemListener(this);
      size.addItemListener(this);
 
      fonts.add(name);
      fonts.add(style);
      fonts.add(size);

      top.add(fonts, BorderLayout.CENTER);

      preview = new JLabel(funny());
//      preview.setEditable(false);
      preview.setOpaque(false);
//      preview.setText(funny());

      JPanel bottom = new JPanel();
      bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
      bottom.add(preview);      

      add(top,    BorderLayout.NORTH);
      add(bottom, BorderLayout.CENTER);
   }

   public void save()
   {
      ClientState.getClientState().setFont(getVariable(), preview.getFont());
   }

   public int getEstimatedWidth()
   {
      return 0;
   }

   public void setAlignWidth(int width)
   {
   }

   public void itemStateChanged(ItemEvent ev)
   {
      Font f = Font.decode(name.getSelectedItem()+"-"+style.getSelectedItem().toString().toUpperCase()+"-"+size.getSelectedItem());

      preview.setFont(f);
      preview.revalidate();

      notifyParent();
   }

   public JComponent getComponent()
   {
      return this;
   }

   public void refresh()
   {
      if (!listing)
      {
         Font f = ClientState.getClientState().getFont(getVariable(), value);

         name.setSelectedItem(f.getFamily());
         size.setSelectedItem(f.getSize()+"");

         if (f.isBold())
         {
            style.setSelectedItem("Bold");
         }
         else if (f.isItalic())
         {
            style.setSelectedItem("Italic");
         }
         else
         {
            style.setSelectedItem("Plain");
         }

         preview.setFont(f);
         preview.validate();
      }
      else
      {
      //
      // obtaining all of the fonts from the system is pretty damned slow so we're going to do it in a thread.
      //
         SwingUtilities.invokeLater(new Runnable()
         {
             public void run()
             {
                String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
                for (int x = 0; x < fonts.length; x++)
                {
                   name.addItem(fonts[x]);
                }

                name.removeItemAt(0);             
                listing = false;
                refresh();
                revalidate();
             } 
         });
      }
   }
}


