package rero.dck;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import rero.config.*;
import rero.dck.*;

public abstract class SuperInput extends JPanel implements DItem
{
   protected String     variable;
   protected DParent    parent;

   public void setEnabled(boolean b)
   {
      disableComponents(this, b);
      super.setEnabled(b);
   }

   private void disableComponents(Container cont, boolean b)
   {
      Component[] blah = cont.getComponents();
      for (int x = 0; x < blah.length; x++)
      {
         blah[x].setEnabled(b);

         if (blah[x] instanceof Container)
         {
            disableComponents((Container)blah[x], b);
         }
      }
   }

   public String getVariable()
   {
      if (parent == null)
      {
         return variable;
      }

      return parent.getVariable(variable);
   }
  
   public void notifyParent()
   {
      if (parent != null)
      {
         save();
         parent.notifyParent(getVariable());
      }
   }

   public void setParent(DParent parent)
   {
      this.parent = parent;
   }

   public JComponent getComponent()
   {
      return this;
   }
}


