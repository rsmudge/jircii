package rero.dck.items;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.File;
import java.beans.*;

import rero.dck.*;
import rero.config.*;

import java.util.*;
import javax.swing.filechooser.*;

public class ImageInput extends SuperInput implements ActionListener
{
   protected JLabel       label;
   protected JTextField   text;
   protected SmallButton  button;
   protected String       value;
   protected JFileChooser chooser;

   protected boolean    directory;

   public ImageInput(String _variable, String _value, String _label, char mnemonic)
   {
      label  = new JLabel(_label);
      text   = new JTextField();
      button = new SmallButton(text.getBorder(), "Click to open an image chooser dialog");

      button.addActionListener(this);

      setLayout(new BorderLayout(2, 2));
     
      add(label,  BorderLayout.WEST);
      add(text,   BorderLayout.CENTER);
      add(button, BorderLayout.EAST);      

      label.setLabelFor(button);
      label.setDisplayedMnemonic(mnemonic);

      variable = _variable;
      value    = _value;
   }

   public void actionPerformed(ActionEvent ev)
   {
      if (chooser == null)
      {
         chooser = new JFileChooser();
         chooser.setApproveButtonText("Select Image");

         chooser.addChoosableFileFilter(new ImageFilter());
         chooser.setAccessory(new ImagePreview(chooser));
      }

      if (chooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION)
      {
         text.setText(chooser.getSelectedFile().getAbsolutePath());
         text.requestFocus();
      }

      notifyParent();
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

   protected class ImagePreview extends JPanel implements PropertyChangeListener
   {
      protected ImageIcon thumbnail = null;
      protected File      file = null;
      protected JLabel    label;

      public ImagePreview(JFileChooser fc)
      {
          setBorder(text.getBorder());
          setPreferredSize(new Dimension(150, 75));
          fc.addPropertyChangeListener(this);
      }

      public void loadImage()
      {
          if (file == null)
          {
             return;
          }

          ImageIcon tmpIcon = new ImageIcon(file.getPath());

          if (tmpIcon.getIconWidth() > (getWidth() - 5))
          {
              thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(getWidth() - 10, -1, Image.SCALE_DEFAULT));

              if (thumbnail.getIconHeight() > (getHeight() - 5))
              {
                 thumbnail = new ImageIcon(thumbnail.getImage().getScaledInstance(-1, getHeight() - 10, Image.SCALE_DEFAULT));
              }
          } 
          else 
          {
              thumbnail = tmpIcon;
          }
       }

       public void paintComponent(Graphics g) 
       {
          super.paintComponent(g);
        
          if (thumbnail == null) 
          {
            loadImage();
          }

          if (thumbnail != null) 
          {
            int x = getWidth()/2 - thumbnail.getIconWidth()/2;
            int y = getHeight()/2 - thumbnail.getIconHeight()/2;

            if (y < 0) 
            {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }

            thumbnail.paintIcon(this, g, x, y);
          }

          paintBorder(g);
       }


       public void propertyChange(PropertyChangeEvent e) 
       {
          String prop = e.getPropertyName();
          if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) 
          {
             file = (File) e.getNewValue();
             if (isShowing()) 
             {
                loadImage();
                repaint();
             }
          }
       }
    }

    protected static class ImageFilter extends FileFilter
    {
       protected static HashSet extensions;
       
       static
       {
           extensions = new HashSet();
           extensions.add(".png");
           extensions.add(".tif");
           extensions.add(".tiff");
           extensions.add(".gif");
           extensions.add(".jpg");
           extensions.add(".jpeg");
       }


       public boolean accept(File f)
       {
          if (f.isDirectory())
          {
             return true;
          }

          String file = f.getName();

          if (file.lastIndexOf('.') > -1)
          {
             String ext = file.substring(file.lastIndexOf('.'), file.length()).toLowerCase();

             return extensions.contains(ext);
          }

           return false;
        }

        public String getDescription() 
        {
           return "Image Files";
        }
    }
}


